package com.adent.publicsite.repository;

import com.adent.publicsite.domain.WebsiteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WebsiteRequestRepository extends JpaRepository<WebsiteRequest, UUID> {
    Optional<WebsiteRequest> findByTenantId(UUID tenantId);
    List<WebsiteRequest> findAllByOrderByCreatedAtDesc();
}
