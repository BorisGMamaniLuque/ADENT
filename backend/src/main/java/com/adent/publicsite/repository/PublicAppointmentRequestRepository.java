package com.adent.publicsite.repository;
import com.adent.publicsite.domain.PublicAppointmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PublicAppointmentRequestRepository extends JpaRepository<PublicAppointmentRequest,UUID>{
 List<PublicAppointmentRequest> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}
