package com.adent.identity.api;

import com.adent.shared.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final CurrentUser current;
    public ProfileController(CurrentUser current) { this.current = current; }

    @GetMapping
    public Map<String, Object> profile(Authentication authentication) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", authentication.getName());
        if (authentication instanceof JwtAuthenticationToken jwt) {
            data.put("roles", jwt.getToken().getClaimAsStringList("roles"));
            data.put("tenantSlug", jwt.getToken().getClaimAsString("tenantSlug"));
            data.put("platformAdmin", Boolean.TRUE.equals(jwt.getToken().getClaim("platformAdmin")));
        }
        try { data.put("tenantId", current.tenantId().toString()); }
        catch (Exception ignored) { data.put("tenantId", null); }
        return data;
    }
}
