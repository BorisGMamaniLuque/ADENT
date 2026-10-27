package com.adent.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adent.jwt")
public record JwtProperties(String secret, long accessTokenMinutes) {}
