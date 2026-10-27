package com.adent.structure.domain;
import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="rooms")
public class Room extends BaseEntity {
    @Column(name="tenant_id", nullable=false) private UUID tenantId;
    @Column(name="floor_id", nullable=false) private UUID floorId;
    @Column(nullable=false, length=100) private String name;
    @Column(length=30) private String code;
    @Column(nullable=false) private boolean active=true;
    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
    public UUID getFloorId(){return floorId;} public void setFloorId(UUID v){floorId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getCode(){return code;} public void setCode(String v){code=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
