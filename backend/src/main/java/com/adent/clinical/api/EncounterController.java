package com.adent.clinical.api;
import com.adent.clinical.domain.Encounter; import com.adent.clinical.service.EncounterService; import jakarta.validation.Valid; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/clinical/encounters") @PreAuthorize("hasAnyRole('OWNER','DENTIST','DENTAL_ASSISTANT')")
public class EncounterController {
 private final EncounterService service; public EncounterController(EncounterService s){service=s;}
 @GetMapping("/patient/{patientId}") public List<Encounter> byPatient(@PathVariable UUID patientId){return service.byPatient(patientId);}
 @PostMapping @PreAuthorize("hasRole('DENTIST')") public Encounter create(@Valid @RequestBody EncounterRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasRole('DENTIST')") public Encounter update(@PathVariable UUID id,@Valid @RequestBody EncounterRequest r){return service.updateDraft(id,r);}
 @PostMapping("/{id}/finalize") @PreAuthorize("hasRole('DENTIST')") public Encounter finalizeOne(@PathVariable UUID id){return service.finalizeEncounter(id);}
 @PostMapping("/{id}/addendum") @PreAuthorize("hasRole('DENTIST')") public Encounter addendum(@PathVariable UUID id,@Valid @RequestBody EncounterRequest r){return service.addendum(id,r);}
}
