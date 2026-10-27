package com.adent.publicsite.repository;
import com.adent.publicsite.domain.PublicService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PublicServiceRepository extends JpaRepository<PublicService,UUID>{
 List<PublicService> findByTenantIdAndActiveTrueOrderByDisplayOrderAscNameAsc(UUID tenantId);
 List<PublicService> findByTenantIdOrderByDisplayOrderAscNameAsc(UUID tenantId);
 void deleteByTenantId(UUID tenantId);
}
