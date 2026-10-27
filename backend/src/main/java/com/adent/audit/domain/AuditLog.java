package com.adent.audit.domain;
import com.adent.shared.domain.BaseEntity; import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="audit_logs")
public class AuditLog extends BaseEntity {
 @Column(name="tenant_id") private UUID tenantId; @Column(nullable=false,length=80) private String username; @Column(nullable=false,length=80) private String action; @Column(name="entity_type",length=80) private String entityType; @Column(name="entity_id") private UUID entityId; @Column(columnDefinition="text") private String details;
 public UUID getTenantId(){return tenantId;} public void setTenantId(UUID v){tenantId=v;} public String getUsername(){return username;} public void setUsername(String v){username=v;} public String getAction(){return action;} public void setAction(String v){action=v;} public String getEntityType(){return entityType;} public void setEntityType(String v){entityType=v;} public UUID getEntityId(){return entityId;} public void setEntityId(UUID v){entityId=v;} public String getDetails(){return details;} public void setDetails(String v){details=v;}
}
