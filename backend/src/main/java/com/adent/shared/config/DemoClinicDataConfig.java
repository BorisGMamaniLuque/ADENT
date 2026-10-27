package com.adent.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

/** Carga opcional e idempotente de datos ficticios. Nunca se habilita por defecto. */
@Configuration
public class DemoClinicDataConfig {
    @Bean
    @Order(2)
    CommandLineRunner demoClinicData(JdbcTemplate db, PasswordEncoder encoder,
            @Value("${adent.demo.data-enabled:false}") boolean demoEnabled,
            @Value("${adent.seed.demo-tenant:demo-dental}") String slug,
            @Value("${adent.seed.owner.username:owner}") String owner) {
        return args -> {
            if (!demoEnabled) return;
            UUID tenantId = db.queryForObject("SELECT id FROM tenants WHERE lower(slug)=lower(?)", UUID.class, slug);
            if (tenantId == null) throw new IllegalStateException("No existe el establecimiento de demostración");
            DemoClinicSeeder.populate(db, encoder, tenantId, owner);
        };
    }
}
