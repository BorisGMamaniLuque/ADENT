package com.adent.clinical.repository;
import com.adent.clinical.domain.ClinicalRecord; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, UUID>{ Optional<ClinicalRecord> findByTenantIdAndPatientId(UUID tenantId, UUID patientId); }
