package com.adent.patient.domain;

import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name="patients")
public class Patient extends BaseEntity {
    @Column(name="tenant_id", nullable=false) private UUID tenantId;
    @Column(name="history_number", nullable=false, length=30) private String historyNumber;
    @Column(name="document_type", length=20) private String documentType;
    @Column(name="document_number", length=30) private String documentNumber;
    @Column(name="first_names", nullable=false, length=120) private String firstNames;
    @Column(name="last_names", nullable=false, length=120) private String lastNames;
    @Column(length=80) private String nationality;
    @Column(length=80) private String country;
    @Column(length=20) private String sex;
    @Column(name="marital_status", length=40) private String maritalStatus;
    @Column(name="birth_date") private LocalDate birthDate;
    @Column(length=30) private String phone;
    @Column(length=160) private String email;
    @Column(length=250) private String address;
    @Column(length=120) private String city;
    @Column(length=160) private String provenance;
    @Column(length=160) private String occupation;
    @Column(name="emergency_contact_name", length=180) private String emergencyContactName;
    @Column(name="emergency_contact_phone", length=40) private String emergencyContactPhone;
    @Column(name="clinical_alerts", columnDefinition="text") private String clinicalAlerts;
    @Column(nullable=false) private boolean active=true;
    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
    public String getHistoryNumber(){return historyNumber;} public void setHistoryNumber(String v){historyNumber=v;}
    public String getDocumentType(){return documentType;} public void setDocumentType(String v){documentType=v;}
    public String getDocumentNumber(){return documentNumber;} public void setDocumentNumber(String v){documentNumber=v;}
    public String getFirstNames(){return firstNames;} public void setFirstNames(String v){firstNames=v;}
    public String getLastNames(){return lastNames;} public void setLastNames(String v){lastNames=v;}
    public String getNationality(){return nationality;} public void setNationality(String v){nationality=v;}
    public String getCountry(){return country;} public void setCountry(String v){country=v;}
    public String getSex(){return sex;} public void setSex(String v){sex=v;}
    public String getMaritalStatus(){return maritalStatus;} public void setMaritalStatus(String v){maritalStatus=v;}
    public LocalDate getBirthDate(){return birthDate;} public void setBirthDate(LocalDate v){birthDate=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getCity(){return city;} public void setCity(String v){city=v;}
    public String getProvenance(){return provenance;} public void setProvenance(String v){provenance=v;}
    public String getOccupation(){return occupation;} public void setOccupation(String v){occupation=v;}
    public String getEmergencyContactName(){return emergencyContactName;} public void setEmergencyContactName(String v){emergencyContactName=v;}
    public String getEmergencyContactPhone(){return emergencyContactPhone;} public void setEmergencyContactPhone(String v){emergencyContactPhone=v;}
    public String getClinicalAlerts(){return clinicalAlerts;} public void setClinicalAlerts(String v){clinicalAlerts=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
