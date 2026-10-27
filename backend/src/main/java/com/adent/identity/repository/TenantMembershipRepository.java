package com.adent.identity.repository;

import com.adent.identity.domain.TenantMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantMembershipRepository extends JpaRepository<TenantMembership, UUID> {
    Optional<TenantMembership> findByTenantIdAndUserIdAndActiveTrue(UUID tenantId, UUID userId);
    List<TenantMembership> findByTenantIdAndActiveTrue(UUID tenantId);
    List<TenantMembership> findByUserIdAndActiveTrue(UUID userId);
}
