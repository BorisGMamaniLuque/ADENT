package com.adent.staff.api;
import jakarta.validation.constraints.NotBlank;
public record StaffRequest(String documentType,String documentNumber,@NotBlank String firstNames,@NotBlank String lastNames,String specialty,String positionName,String phone,String email){}
