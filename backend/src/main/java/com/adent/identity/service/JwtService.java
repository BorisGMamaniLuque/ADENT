package com.adent.identity.service;

import com.adent.shared.security.JwtProperties;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final JwtProperties props;

    public JwtService(JwtEncoder encoder, JwtProperties props) {
        this.encoder = encoder;
        this.props = props;
    }

    public TokenResult issue(String username, UUID tenantId, UUID membershipId, String tenantSlug,
                             List<String> roles, boolean platformAdmin) {
        Instant now = Instant.now();
        Instant expires = now.plus(props.accessTokenMinutes(), ChronoUnit.MINUTES);
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("adent")
                .issuedAt(now)
                .expiresAt(expires)
                .subject(username)
                .claim("roles", roles)
                .claim("platformAdmin", platformAdmin);
        if (tenantId != null) claims.claim("tenantId", tenantId.toString());
        if (membershipId != null) claims.claim("membershipId", membershipId.toString());
        if (tenantSlug != null) claims.claim("tenantSlug", tenantSlug);
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String value = encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
        return new TokenResult(value, expires);
    }

    public record TokenResult(String value, Instant expiresAt) {}
}
