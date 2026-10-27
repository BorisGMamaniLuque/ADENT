package com.adent.structure.repository;
import com.adent.structure.domain.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface FloorRepository extends JpaRepository<Floor, UUID> {
    List<Floor> findByTenantIdOrderByFloorNumberAsc(UUID tenantId);
    List<Floor> findByTenantIdAndBranchIdOrderByFloorNumberAsc(UUID tenantId, UUID branchId);
    Optional<Floor> findByIdAndTenantId(UUID id, UUID tenantId);
}
