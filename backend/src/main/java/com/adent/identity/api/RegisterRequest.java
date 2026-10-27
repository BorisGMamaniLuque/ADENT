package com.adent.identity.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 180) String establishmentName,
        @Size(max = 180) String legalName,
        @Size(max = 40) String establishmentType,
        @Size(max = 20) String ruc,
        @Size(max = 250) String address,
        @Size(max = 30) String establishmentPhone,
        @Email @Size(max = 160) String establishmentEmail,
        @NotBlank @Size(max = 120) String ownerFirstNames,
        @NotBlank @Size(max = 120) String ownerLastNames,
        @Size(max = 20) String ownerDocumentType,
        @Size(max = 30) String ownerDocumentNumber,
        @Size(max = 120) String ownerSpecialty,
        @NotBlank @Size(min = 4, max = 80) String username,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(min = 8, max = 100) String password
) {}
