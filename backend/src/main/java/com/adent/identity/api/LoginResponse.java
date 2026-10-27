package com.adent.identity.api;

import java.time.Instant;
import java.util.List;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        String username,
        String tenantId,
        String tenantSlug,
        List<String> roles,
        boolean platformAdmin,
        String organizationName,
        String organizationLogoText,
        String organizationLogoUrl
) {}
