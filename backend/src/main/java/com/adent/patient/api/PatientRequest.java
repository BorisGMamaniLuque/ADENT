package com.adent.patient.api;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
public record PatientRequest(String documentType, String documentNumber, @NotBlank String firstNames, @NotBlank String lastNames,
                             String nationality, String country, String sex, String maritalStatus, LocalDate birthDate,
                             String phone, String email, String address, String city, String provenance, String occupation,
                             String emergencyContactName, String emergencyContactPhone, String clinicalAlerts) {}
