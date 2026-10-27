package com.adent.identity.domain;

import com.adent.shared.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "membership_roles", uniqueConstraints = @UniqueConstraint(columnNames = {"membership_id", "role_id"}))
public class MembershipRole extends BaseEntity {
    @Column(name = "membership_id", nullable = false)
    private UUID membershipId;
    @Column(name = "role_id", nullable = false)
    private UUID roleId;
    public UUID getMembershipId() { return membershipId; }
    public void setMembershipId(UUID membershipId) { this.membershipId = membershipId; }
    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
}
