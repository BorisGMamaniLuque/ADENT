package com.adent.publicsite.repository;
import com.adent.publicsite.domain.PublicGalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PublicGalleryRepository extends JpaRepository<PublicGalleryItem,UUID>{
 List<PublicGalleryItem> findByTenantIdAndActiveTrueOrderByDisplayOrderAsc(UUID tenantId);
 List<PublicGalleryItem> findByTenantIdOrderByDisplayOrderAsc(UUID tenantId);
 void deleteByTenantId(UUID tenantId);
}
