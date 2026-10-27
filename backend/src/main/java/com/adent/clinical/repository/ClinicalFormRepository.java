package com.adent.clinical.repository;
import com.adent.clinical.domain.ClinicalForm; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface ClinicalFormRepository extends JpaRepository<ClinicalForm,UUID>{ List<ClinicalForm> findByTenantIdAndPatientIdOrderByRecordedAtDesc(UUID tenantId,UUID patientId); Optional<ClinicalForm> findFirstByTenantIdAndPatientIdAndFormTypeOrderByRecordedAtDesc(UUID tenantId,UUID patientId,String formType); }
