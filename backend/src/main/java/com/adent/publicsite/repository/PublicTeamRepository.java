package com.adent.publicsite.repository;
import com.adent.publicsite.domain.PublicTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PublicTeamRepository extends JpaRepository<PublicTeamMember,UUID>{
 List<PublicTeamMember> findByTenantIdAndActiveTrueOrderByDisplayOrderAscFullNameAsc(UUID tenantId);
 List<PublicTeamMember> findByTenantIdOrderByDisplayOrderAscFullNameAsc(UUID tenantId);
 void deleteByTenantId(UUID tenantId);
}
