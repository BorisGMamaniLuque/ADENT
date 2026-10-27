package com.adent.publicsite.repository;
import com.adent.publicsite.domain.PublicSite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PublicSiteRepository extends JpaRepository<PublicSite,UUID>{
 Optional<PublicSite> findByTenantId(UUID tenantId);
 Optional<PublicSite> findBySiteSlugIgnoreCase(String siteSlug);
}
