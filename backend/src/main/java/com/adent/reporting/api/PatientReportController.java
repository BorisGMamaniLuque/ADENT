package com.adent.reporting.api;

import com.adent.clinical.repository.EncounterRepository;
import com.adent.odontogram.repository.OdontogramRepository;
import com.adent.patient.api.PatientResponse;
import com.adent.patient.service.PatientService;
import com.adent.shared.security.CurrentUser;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports/patient")
@PreAuthorize("hasAnyRole('OWNER','RECEPTION','DENTIST')")
public class PatientReportController {

    private final PatientService patients;
    private final EncounterRepository encounters;
    private final OdontogramRepository odontograms;
    private final CurrentUser current;
    private final JdbcTemplate jdbc;

    public PatientReportController(
            PatientService patients,
            EncounterRepository encounters,
            OdontogramRepository odontograms,
            CurrentUser current,
            JdbcTemplate jdbc
    ) {
        this.patients = patients;
        this.encounters = encounters;
        this.odontograms = odontograms;
        this.current = current;
        this.jdbc = jdbc;
    }

    @GetMapping("/{patientId}")
    public Map<String, Object> summary(@PathVariable UUID patientId) {
        PatientResponse patient = patients.get(patientId);
        var patientEncounters = encounters.findByTenantIdAndPatientIdOrderByEncounteredAtDesc(
                current.tenantId(), patientId);
        var odontogramVersions = odontograms.findByTenantIdAndPatientIdOrderByVersionNoDesc(
                current.tenantId(), patientId);

        BigDecimal totalPaid = jdbc.queryForObject(
                "select coalesce(sum(amount), 0) from payments where tenant_id = ? and patient_id = ?",
                BigDecimal.class,
                current.tenantId(),
                patientId
        );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("patient", patient);
        response.put("encounters", patientEncounters);
        response.put("odontogramVersions", odontogramVersions.size());
        response.put("totalPaid", totalPaid == null ? BigDecimal.ZERO : totalPaid);
        return response;
    }

    @GetMapping(value = "/{patientId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable UUID patientId) throws Exception {
        PatientResponse patient = patients.get(patientId);
        var patientEncounters = encounters.findByTenantIdAndPatientIdOrderByEncounteredAtDesc(
                current.tenantId(), patientId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        document.add(new Paragraph(
                "ADENT - Resumen del paciente",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
        ));
        document.add(new Paragraph("Historia clínica: " + safe(patient.historyNumber())));
        document.add(new Paragraph("Paciente: " + safe(patient.firstNames()) + " " + safe(patient.lastNames())));
        document.add(new Paragraph("Documento: " + safe(patient.documentType()) + " " + safe(patient.documentNumber())));
        document.add(new Paragraph("Teléfono: " + safe(patient.phone())));
        document.add(new Paragraph("Correo: " + safe(patient.email())));
        document.add(new Paragraph("Dirección: " + safe(patient.address())));
        document.add(new Paragraph("Alertas clínicas: " + safe(patient.clinicalAlerts())));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Atenciones registradas: " + patientEncounters.size()));

        for (var encounter : patientEncounters) {
            document.add(new Paragraph(
                    "- " + encounter.getEncounteredAt()
                            + " | " + safe(encounter.getChiefComplaint())
                            + " | " + encounter.getStatus()
            ));
        }

        document.close();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=ADENT_" + safe(patient.historyNumber()) + ".pdf"
                )
                .body(out.toByteArray());
    }

    @GetMapping(
            value = "/{patientId}/xlsx",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public ResponseEntity<byte[]> xlsx(@PathVariable UUID patientId) throws Exception {
        PatientResponse patient = patients.get(patientId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet patientSheet = workbook.createSheet("Paciente");
            String[][] rows = {
                    {"Historia", safe(patient.historyNumber())},
                    {"Paciente", safe(patient.firstNames()) + " " + safe(patient.lastNames())},
                    {"Documento", safe(patient.documentType()) + " " + safe(patient.documentNumber())},
                    {"Teléfono", safe(patient.phone())},
                    {"Correo", safe(patient.email())},
                    {"Dirección", safe(patient.address())}
            };

            for (int i = 0; i < rows.length; i++) {
                Row row = patientSheet.createRow(i);
                row.createCell(0).setCellValue(rows[i][0]);
                row.createCell(1).setCellValue(rows[i][1]);
            }

            Sheet encounterSheet = workbook.createSheet("Atenciones");
            Row header = encounterSheet.createRow(0);
            header.createCell(0).setCellValue("Fecha");
            header.createCell(1).setCellValue("Motivo");
            header.createCell(2).setCellValue("Estado");

            int rowIndex = 1;
            for (var encounter : encounters.findByTenantIdAndPatientIdOrderByEncounteredAtDesc(
                    current.tenantId(), patientId)) {
                Row row = encounterSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(String.valueOf(encounter.getEncounteredAt()));
                row.createCell(1).setCellValue(safe(encounter.getChiefComplaint()));
                row.createCell(2).setCellValue(String.valueOf(encounter.getStatus()));
            }

            patientSheet.autoSizeColumn(0);
            patientSheet.autoSizeColumn(1);
            encounterSheet.autoSizeColumn(0);
            encounterSheet.autoSizeColumn(1);
            encounterSheet.autoSizeColumn(2);

            workbook.write(out);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=ADENT_" + safe(patient.historyNumber()) + ".xlsx"
                    )
                    .body(out.toByteArray());
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
