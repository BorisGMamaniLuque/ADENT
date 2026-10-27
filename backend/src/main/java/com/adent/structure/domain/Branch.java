package com.adent.structure.domain;
import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="branches")
public class Branch extends BaseEntity {
    @Column(name="tenant_id", nullable=false) private UUID tenantId;
    @Column(nullable=false, length=120) private String name;
    @Column(length=250) private String address;
    @Column(nullable=false) private boolean active=true;
    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
