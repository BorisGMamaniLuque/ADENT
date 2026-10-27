package com.adent.shared.security;

import com.adent.shared.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CurrentUser {
    private Jwt jwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new BusinessException("No existe un usuario autenticado");
        }
        return jwt;
    }

    public String username() { return jwt().getSubject(); }

    public UUID tenantId() {
        String value = jwt().getClaimAsString("tenantId");
        if (value == null || value.isBlank()) throw new BusinessException("El token no tiene tenant asignado");
        return UUID.fromString(value);
    }

    public UUID membershipId() {
        String value = jwt().getClaimAsString("membershipId");
        return value == null || value.isBlank() ? null : UUID.fromString(value);
    }

    public boolean platformAdmin() {
        Boolean value = jwt().getClaim("platformAdmin");
        return Boolean.TRUE.equals(value);
    }

    public List<String> roles() {
        List<String> roles = jwt().getClaimAsStringList("roles");
        return roles == null ? List.of() : roles;
    }
}
