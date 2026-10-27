package com.adent.structure.domain;
import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="floors")
public class Floor extends BaseEntity {
    @Column(name="tenant_id", nullable=false) private UUID tenantId;
    @Column(name="branch_id", nullable=false) private UUID branchId;
    @Column(nullable=false, length=80) private String name;
    @Column(name="floor_number") private Integer floorNumber;
    public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;}
    public UUID getBranchId(){return branchId;} public void setBranchId(UUID v){branchId=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public Integer getFloorNumber(){return floorNumber;} public void setFloorNumber(Integer v){floorNumber=v;}
}
