package com.adent.clinical.repository;
import com.adent.clinical.domain.Encounter; import org.springframework.data.jpa.repository.JpaRepository; import java.time.OffsetDateTime; import java.util.*;
public interface EncounterRepository extends JpaRepository<Encounter,UUID>{
 List<Encounter> findByTenantIdAndPatientIdOrderByEncounteredAtDesc(UUID tenantId,UUID patientId);
 List<Encounter> findByTenantIdAndEncounteredAtBetweenOrderByEncounteredAt(UUID tenantId, OffsetDateTime from, OffsetDateTime to);
 Optional<Encounter> findByIdAndTenantId(UUID id,UUID tenantId);
 long countByTenantId(UUID tenantId);
}
