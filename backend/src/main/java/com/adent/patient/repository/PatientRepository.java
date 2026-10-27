package com.adent.patient.repository;
import com.adent.patient.domain.Patient;
import org.springframework.data.domain.*; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
import java.util.*;
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByTenantIdAndDocumentTypeIgnoreCaseAndDocumentNumberIgnoreCase(UUID tenantId, String documentType, String documentNumber);
    Optional<Patient> findByIdAndTenantId(UUID id, UUID tenantId);
    Optional<Patient> findByTenantIdAndHistoryNumber(UUID tenantId, String historyNumber);
    long countByTenantId(UUID tenantId);
    List<Patient> findByTenantId(UUID tenantId);
    @Query("""
        select p from Patient p where p.tenantId=:tenantId and p.active=true and
        (lower(p.firstNames) like lower(concat('%',:q,'%')) or lower(p.lastNames) like lower(concat('%',:q,'%'))
         or lower(coalesce(p.documentNumber,'')) like lower(concat('%',:q,'%'))
         or lower(p.historyNumber) like lower(concat('%',:q,'%')))
        """)
    Page<Patient> search(@Param("tenantId") UUID tenantId, @Param("q") String q, Pageable pageable);
}
