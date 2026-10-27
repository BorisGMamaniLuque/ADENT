package com.adent.patient.api;
import com.adent.patient.domain.Patient; import java.time.LocalDate; import java.time.Period; import java.util.UUID;
public record PatientResponse(UUID id, String historyNumber, String documentType, String documentNumber, String firstNames,
                              String lastNames, String nationality, String country, String sex, String maritalStatus,
                              LocalDate birthDate, Integer age, String phone, String email, String address, String city,
                              String provenance, String occupation, String emergencyContactName, String emergencyContactPhone,
                              String clinicalAlerts, boolean active) {
    public static PatientResponse from(Patient p){Integer age=p.getBirthDate()==null?null:Period.between(p.getBirthDate(), LocalDate.now()).getYears();
        return new PatientResponse(p.getId(),p.getHistoryNumber(),p.getDocumentType(),p.getDocumentNumber(),p.getFirstNames(),p.getLastNames(),p.getNationality(),p.getCountry(),p.getSex(),p.getMaritalStatus(),p.getBirthDate(),age,p.getPhone(),p.getEmail(),p.getAddress(),p.getCity(),p.getProvenance(),p.getOccupation(),p.getEmergencyContactName(),p.getEmergencyContactPhone(),p.getClinicalAlerts(),p.isActive());}
}
