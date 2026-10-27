package com.adent.appointment.repository;
import com.adent.appointment.domain.Appointment; import org.springframework.data.jpa.repository.JpaRepository; import java.time.OffsetDateTime; import java.util.*;
public interface AppointmentRepository extends JpaRepository<Appointment,UUID>{ List<Appointment> findByTenantIdAndStartsAtBetweenOrderByStartsAt(UUID tenantId, OffsetDateTime from, OffsetDateTime to); Optional<Appointment> findByIdAndTenantId(UUID id, UUID tenantId); }
