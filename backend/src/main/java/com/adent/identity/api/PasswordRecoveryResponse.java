package com.adent.identity.api;

public record PasswordRecoveryResponse(String message, String maskedDestination, Integer expiresInMinutes, String developmentCode) {}
