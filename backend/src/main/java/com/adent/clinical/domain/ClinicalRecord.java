package com.adent.clinical.domain;
import com.adent.shared.domain.BaseEntity; import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="clinical_records")
public class ClinicalRecord extends BaseEntity {
    @Column(name="tenant_id", nullable=false) private UUID tenantId;
    @Column(name="patient_id", nullable=false, unique=true) private UUID patientId;
    @Column(name="summary", columnDefinition="text") private String summary;
    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;} public UUID getPatientId(){return patientId;} public void setPatientId(UUID v){patientId=v;} public String getSummary(){return summary;} public void setSummary(String v){summary=v;}
}
