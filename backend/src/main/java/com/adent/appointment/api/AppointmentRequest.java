package com.adent.appointment.api;
import com.adent.appointment.domain.AppointmentStatus; import jakarta.validation.constraints.NotNull; import java.time.OffsetDateTime; import java.util.UUID;
public record AppointmentRequest(@NotNull UUID patientId,@NotNull UUID staffId,UUID roomId,@NotNull OffsetDateTime startsAt,OffsetDateTime endsAt,String reason,AppointmentStatus status) {}
