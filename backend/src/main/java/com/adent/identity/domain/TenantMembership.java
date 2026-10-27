package com.adent.identity.domain;

import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tenant_memberships")
public class TenantMembership extends BaseEntity {
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "staff_id")
    private UUID staffId;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "default_membership", nullable = false)
    private boolean defaultMembership;

    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getStaffId() { return staffId; }
    public void setStaffId(UUID staffId) { this.staffId = staffId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isDefaultMembership() { return defaultMembership; }
    public void setDefaultMembership(boolean defaultMembership) { this.defaultMembership = defaultMembership; }
}
