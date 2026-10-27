package com.adent.structure.repository;
import com.adent.structure.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BranchRepository extends JpaRepository<Branch, UUID> {
    List<Branch> findByTenantIdOrderByName(UUID tenantId);
    Optional<Branch> findByIdAndTenantId(UUID id, UUID tenantId);
}
