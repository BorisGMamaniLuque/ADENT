package com.adent.staff.repository;
import com.adent.staff.domain.Staff; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface StaffRepository extends JpaRepository<Staff, UUID>{ List<Staff> findByTenantIdOrderByLastNamesAscFirstNamesAsc(UUID tenantId); Optional<Staff> findByIdAndTenantId(UUID id, UUID tenantId); }
