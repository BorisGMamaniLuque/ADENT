package com.adent.reporting.api;

import com.adent.reporting.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('OWNER','ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(
            @RequestParam(defaultValue = "MONTH") String preset,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID staffId,
            @RequestParam(required = false) UUID roomId
    ) {
        return dashboardService.dashboard(preset, from, to, staffId, roomId);
    }
    @GetMapping(value="/dashboard/export/pdf", produces=MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(defaultValue="MONTH") String preset,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required=false) UUID staffId, @RequestParam(required=false) UUID roomId) throws Exception {
        DashboardResponse d = dashboardService.dashboard(preset, from, to, staffId, roomId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter.getInstance(doc, out);
        doc.open();
        doc.add(new Paragraph("ADENT | Reporte de gestion"));
        doc.add(new Paragraph("Periodo: " + d.periodLabel()));
        doc.add(new Paragraph("Pacientes: " + d.patients()));
        doc.add(new Paragraph("Pacientes nuevos: " + d.newPatients()));
        doc.add(new Paragraph("Atenciones: " + d.encounters()));
        doc.add(new Paragraph("Citas atendidas: " + d.completedAppointments()));
        doc.add(new Paragraph("Citas canceladas: " + d.cancelledAppointments()));
        doc.add(new Paragraph("Personal activo: " + d.staff()));
        doc.add(new Paragraph("Ingresos: " + (d.incomeAvailable() ? "S/ " + d.income() : "No disponible")));
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Atenciones por odontologo"));
        for (var item : d.attentionsByDentist()) doc.add(new Paragraph(item.name() + ": " + item.value()));
        doc.add(new Paragraph("Atenciones por consultorio"));
        for (var item : d.attentionsByRoom()) doc.add(new Paragraph(item.name() + ": " + item.value()));
        doc.close();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=ADENT_dashboard.pdf").body(out.toByteArray());
    }

    @GetMapping(value="/dashboard/export/xlsx", produces="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> exportXlsx(
            @RequestParam(defaultValue="MONTH") String preset,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required=false) UUID staffId, @RequestParam(required=false) UUID roomId) throws Exception {
        DashboardResponse d = dashboardService.dashboard(preset, from, to, staffId, roomId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Resumen");
            String[][] rows = {
                {"Indicador", "Valor"}, {"Periodo", d.periodLabel()},
                {"Pacientes", String.valueOf(d.patients())}, {"Pacientes nuevos", String.valueOf(d.newPatients())},
                {"Atenciones", String.valueOf(d.encounters())},
                {"Citas atendidas", String.valueOf(d.completedAppointments())},
                {"Citas canceladas", String.valueOf(d.cancelledAppointments())},
                {"Personal activo", String.valueOf(d.staff())},
                {"Ingresos (S/)", d.incomeAvailable() ? String.valueOf(d.income()) : "No disponible"}
            };
            for (int i=0;i<rows.length;i++) {
                var row=sheet.createRow(i);
                row.createCell(0).setCellValue(rows[i][0]);
                row.createCell(1).setCellValue(rows[i][1]);
            }
            sheet.autoSizeColumn(0); sheet.autoSizeColumn(1);
            var doctors = workbook.createSheet("Odontologos");
            int r=0;
            for(var value : d.attentionsByDentist()) {
                var row=doctors.createRow(r++);
                row.createCell(0).setCellValue(value.name());
                row.createCell(1).setCellValue(value.value());
            }
            var offices=workbook.createSheet("Consultorios"); r=0;
            for(var value : d.attentionsByRoom()) {
                var row=offices.createRow(r++);
                row.createCell(0).setCellValue(value.name());
                row.createCell(1).setCellValue(value.value());
            }
            workbook.write(out);
        }
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=ADENT_dashboard.xlsx").body(out.toByteArray());
    }

}
