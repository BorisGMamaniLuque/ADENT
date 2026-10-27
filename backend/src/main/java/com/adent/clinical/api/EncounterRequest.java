package com.adent.clinical.api;
import jakarta.validation.constraints.NotNull; import java.time.OffsetDateTime; import java.util.UUID;
public record EncounterRequest(@NotNull UUID patientId,@NotNull UUID professionalStaffId,UUID assistantStaffId,UUID roomId,UUID appointmentId,OffsetDateTime encounteredAt,String chiefComplaint,String anamnesis,String clinicalExam,String dentalAssessment,String treatmentPlanSummary,String proceduresSummary,String indications,String evolution,OffsetDateTime nextAttentionAt) {}
