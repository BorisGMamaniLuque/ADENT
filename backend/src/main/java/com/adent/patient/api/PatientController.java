package com.adent.patient.api;
import com.adent.patient.service.PatientService; import jakarta.validation.Valid; import org.springframework.data.domain.*; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*; import java.util.UUID;
@RestController @RequestMapping("/api/patients") @PreAuthorize("hasAnyRole('OWNER','RECEPTION','DENTIST','DENTAL_ASSISTANT','LAB_TECHNICIAN','XRAY_TECHNICIAN')")
public class PatientController {
 private final PatientService service; public PatientController(PatientService service){this.service=service;}
 @GetMapping public Page<PatientResponse> search(@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return service.search(q,PageRequest.of(page,Math.min(size,100),Sort.by("lastNames").ascending()));}
 @GetMapping("/{id}") public PatientResponse get(@PathVariable UUID id){return service.get(id);}
 @PostMapping @PreAuthorize("hasAnyRole('OWNER','RECEPTION','DENTIST','DENTAL_ASSISTANT')") public PatientResponse create(@Valid @RequestBody PatientRequest r){return service.create(r);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('OWNER','RECEPTION','DENTIST')") public PatientResponse update(@PathVariable UUID id,@Valid @RequestBody PatientRequest r){return service.update(id,r);}
}
