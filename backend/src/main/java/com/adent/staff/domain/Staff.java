package com.adent.staff.domain;
import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="staff")
public class Staff extends BaseEntity {
    @Column(name="tenant_id", nullable=false) private UUID tenantId;
    @Column(name="document_type", length=20) private String documentType;
    @Column(name="document_number", length=30) private String documentNumber;
    @Column(name="first_names", nullable=false, length=120) private String firstNames;
    @Column(name="last_names", nullable=false, length=120) private String lastNames;
    @Column(length=120) private String specialty;
    @Column(name="position_name", length=120) private String positionName;
    @Column(length=30) private String phone;
    @Column(length=160) private String email;
    @Column(nullable=false) private boolean active=true;
    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
    public String getDocumentType(){return documentType;} public void setDocumentType(String v){documentType=v;}
    public String getDocumentNumber(){return documentNumber;} public void setDocumentNumber(String v){documentNumber=v;}
    public String getFirstNames(){return firstNames;} public void setFirstNames(String v){firstNames=v;}
    public String getLastNames(){return lastNames;} public void setLastNames(String v){lastNames=v;}
    public String getSpecialty(){return specialty;} public void setSpecialty(String v){specialty=v;}
    public String getPositionName(){return positionName;} public void setPositionName(String v){positionName=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
