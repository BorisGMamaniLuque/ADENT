package com.adent.patient.service;

import com.adent.clinical.domain.ClinicalRecord;
import com.adent.audit.service.AuditService;
import com.adent.clinical.repository.ClinicalRecordRepository;
import com.adent.patient.api.*;
import com.adent.patient.domain.Patient;
import com.adent.patient.repository.PatientRepository;
import com.adent.shared.exception.BusinessException;
import com.adent.shared.exception.NotFoundException;
import com.adent.shared.security.CurrentUser;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale; import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patients; private final ClinicalRecordRepository records; private final CurrentUser current; private final AuditService audit;
    public PatientService(PatientRepository patients, ClinicalRecordRepository records, CurrentUser current, AuditService audit){this.patients=patients;this.records=records;this.current=current;this.audit=audit;}
    @Transactional public PatientResponse create(PatientRequest r){UUID tenant=current.tenantId(); String dt=upperClean(r.documentType()); String dn=upperClean(r.documentNumber()); if(dt!=null&&dn!=null&&patients.existsByTenantIdAndDocumentTypeIgnoreCaseAndDocumentNumberIgnoreCase(tenant,dt,dn))throw new BusinessException("Ya existe un paciente con ese documento en este establecimiento"); Patient p=new Patient();p.setTenantId(tenant);apply(p,r);p.setHistoryNumber(nextHistoryNumber());p=patients.save(p);ClinicalRecord cr=new ClinicalRecord();cr.setTenantId(tenant);cr.setPatientId(p.getId());records.save(cr);audit.log("PATIENT_CREATED","Patient",p.getId(),"Historia "+p.getHistoryNumber());return PatientResponse.from(p);}
    @Transactional(readOnly=true) public PatientResponse get(UUID id){return PatientResponse.from(patients.findByIdAndTenantId(id,current.tenantId()).orElseThrow(()->new NotFoundException("Paciente no encontrado")));}
    @Transactional(readOnly=true) public Page<PatientResponse> search(String q, Pageable pageable){return patients.search(current.tenantId(),q==null?"":q.trim(),pageable).map(PatientResponse::from);}
    @Transactional public PatientResponse update(UUID id, PatientRequest r){Patient p=patients.findByIdAndTenantId(id,current.tenantId()).orElseThrow(()->new NotFoundException("Paciente no encontrado"));String dt=upperClean(r.documentType()),dn=upperClean(r.documentNumber());if(dt!=null&&dn!=null&&(!dt.equalsIgnoreCase(p.getDocumentType()==null?"":p.getDocumentType())||!dn.equalsIgnoreCase(p.getDocumentNumber()==null?"":p.getDocumentNumber()))&&patients.existsByTenantIdAndDocumentTypeIgnoreCaseAndDocumentNumberIgnoreCase(current.tenantId(),dt,dn))throw new BusinessException("El documento ya pertenece a otro paciente");apply(p,r);Patient saved=patients.save(p);audit.log("PATIENT_UPDATED","Patient",saved.getId(),"Patient Master Record actualizado");return PatientResponse.from(saved);}
    private void apply(Patient p, PatientRequest r){p.setDocumentType(upperClean(r.documentType()));p.setDocumentNumber(upperClean(r.documentNumber()));p.setFirstNames(r.firstNames().trim());p.setLastNames(r.lastNames().trim());p.setNationality(clean(r.nationality()));p.setCountry(clean(r.country()));p.setSex(clean(r.sex()));p.setMaritalStatus(clean(r.maritalStatus()));p.setBirthDate(r.birthDate());p.setPhone(clean(r.phone()));p.setEmail(clean(r.email()));p.setAddress(clean(r.address()));p.setCity(clean(r.city()));p.setProvenance(clean(r.provenance()));p.setOccupation(clean(r.occupation()));p.setEmergencyContactName(clean(r.emergencyContactName()));p.setEmergencyContactPhone(clean(r.emergencyContactPhone()));p.setClinicalAlerts(clean(r.clinicalAlerts()));}
    private String nextHistoryNumber(){return "HC-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT);} private String clean(String v){return v==null||v.isBlank()?null:v.trim();} private String upperClean(String v){String x=clean(v);return x==null?null:x.toUpperCase(Locale.ROOT);}
}
