package com.adent.identity.service.recovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class ConfigurableRecoveryDeliveryService implements RecoveryDeliveryService {
    private static final Logger log = LoggerFactory.getLogger(ConfigurableRecoveryDeliveryService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final RestClient.Builder restClientBuilder;
    private final boolean emailEnabled;
    private final String emailFrom;
    private final boolean smsEnabled;
    private final String twilioAccountSid;
    private final String twilioAuthToken;
    private final String twilioFromNumber;

    public ConfigurableRecoveryDeliveryService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            RestClient.Builder restClientBuilder,
            @Value("${adent.recovery.email.enabled:false}") boolean emailEnabled,
            @Value("${adent.recovery.email.from:no-reply@adent.local}") String emailFrom,
            @Value("${adent.recovery.sms.enabled:false}") boolean smsEnabled,
            @Value("${adent.recovery.sms.twilio.account-sid:}") String twilioAccountSid,
            @Value("${adent.recovery.sms.twilio.auth-token:}") String twilioAuthToken,
            @Value("${adent.recovery.sms.twilio.from-number:}") String twilioFromNumber
    ) {
        this.mailSenderProvider = mailSenderProvider;
        this.restClientBuilder = restClientBuilder;
        this.emailEnabled = emailEnabled;
        this.emailFrom = emailFrom;
        this.smsEnabled = smsEnabled;
        this.twilioAccountSid = twilioAccountSid;
        this.twilioAuthToken = twilioAuthToken;
        this.twilioFromNumber = twilioFromNumber;
    }

    @Override
    public void deliver(String channel, String destination, String code, int expiresInMinutes) {
        if ("EMAIL".equals(channel) && emailEnabled) {
            sendEmail(destination, code, expiresInMinutes);
            return;
        }
        if ("SMS".equals(channel) && smsEnabled) {
            sendSms(destination, code, expiresInMinutes);
            return;
        }

        // Modo desarrollo: el AuthService puede devolver el código si RECOVERY_DEV_SHOW_CODE=true.
        log.info("ADENT recovery dev delivery channel={} destination={} code={} expires={}min",
                channel, destination, code, expiresInMinutes);
    }

    private void sendEmail(String destination, String code, int expiresInMinutes) {
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null) throw new IllegalStateException("SMTP está habilitado pero JavaMailSender no está configurado");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(destination);
        message.setSubject("Código de recuperación ADENT");
        message.setText("Tu código de recuperación ADENT es " + code +
                ". Vence en " + expiresInMinutes + " minutos. Si no solicitaste este cambio, ignora este mensaje.");
        sender.send(message);
    }

    private void sendSms(String destination, String code, int expiresInMinutes) {
        if (isBlank(twilioAccountSid) || isBlank(twilioAuthToken) || isBlank(twilioFromNumber)) {
            throw new IllegalStateException("SMS está habilitado pero faltan credenciales de Twilio");
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("To", destination);
        form.add("From", twilioFromNumber);
        form.add("Body", "ADENT: tu código de recuperación es " + code + ". Vence en " + expiresInMinutes + " min.");

        restClientBuilder
                .baseUrl("https://api.twilio.com/2010-04-01/Accounts/" + twilioAccountSid)
                .defaultHeaders(headers -> headers.setBasicAuth(twilioAccountSid, twilioAuthToken))
                .build()
                .post()
                .uri("/Messages.json")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
