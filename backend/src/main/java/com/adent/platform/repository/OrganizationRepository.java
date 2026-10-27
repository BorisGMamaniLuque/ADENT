package com.adent.platform.repository;

import com.adent.platform.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByTenantId(UUID tenantId);
    boolean existsByRuc(String ruc);
}
