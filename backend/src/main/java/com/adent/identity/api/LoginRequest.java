package com.adent.identity.api;

import jakarta.validation.constraints.NotBlank;

/** Login simple. El tenant se resuelve internamente desde la membresía del usuario. */
public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
