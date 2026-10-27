package com.adent.identity.api;

import jakarta.validation.constraints.NotBlank;

public record PasswordRecoveryRequest(@NotBlank String channel, @NotBlank String destination) {}
