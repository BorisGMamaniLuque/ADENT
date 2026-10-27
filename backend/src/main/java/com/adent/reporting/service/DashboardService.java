package com.adent.reporting.service;

import com.adent.appointment.domain.Appointment;
import com.adent.appointment.domain.AppointmentStatus;
import com.adent.appointment.repository.AppointmentRepository;
import com.adent.clinical.domain.Encounter;
import com.adent.clinical.repository.EncounterRepository;
import com.adent.patient.domain.Patient;
import com.adent.patient.repository.PatientRepository;
import com.adent.reporting.api.DashboardResponse;
import com.adent.shared.security.CurrentUser;
import com.adent.staff.domain.Staff;
import com.adent.staff.repository.StaffRepository;
import com.adent.structure.domain.Room;
import com.adent.structure.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final ZoneId ZONE = ZoneId.of("America/Lima");
    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es", "PE"));

    private final CurrentUser current;
    private final PatientRepository patients;
    private final EncounterRepository encounters;
    private final AppointmentRepository appointments;
    private final StaffRepository staff;
    private final RoomRepository rooms;

    public DashboardService(CurrentUser current,
                            PatientRepository patients,
                            EncounterRepository encounters,
                            AppointmentRepository appointments,
                            StaffRepository staff,
                            RoomRepository rooms) {
        this.current = current;
        this.patients = patients;
        this.encounters = encounters;
        this.appointments = appointments;
        this.staff = staff;
        this.rooms = rooms;
    }

    public DashboardResponse dashboard(String preset,
                                       LocalDate from,
                                       LocalDate to,
                                       UUID staffId,
                                       UUID roomId) {

        UUID tenantId = current.tenantId();
        DateRange range = resolveRange(preset, from, to);

        List<Staff> tenantStaff = staff.findByTenantIdOrderByLastNamesAscFirstNamesAsc(tenantId);
        List<Room> tenantRooms = rooms.findByTenantIdOrderByName(tenantId);
        Map<UUID, Staff> staffById = tenantStaff.stream().collect(Collectors.toMap(Staff::getId, Function.identity()));
        Map<UUID, Room> roomById = tenantRooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));

        List<Encounter> selectedEncounters = encounters
                .findByTenantIdAndEncounteredAtBetweenOrderByEncounteredAt(tenantId, range.from(), range.toExclusive())
                .stream()
                .filter(e -> staffId == null || staffId.equals(e.getProfessionalStaffId()))
                .filter(e -> roomId == null || roomId.equals(e.getRoomId()))
                .toList();

        List<Appointment> selectedAppointments = appointments
                .findByTenantIdAndStartsAtBetweenOrderByStartsAt(tenantId, range.from(), range.toExclusive())
                .stream()
                .filter(a -> staffId == null || staffId.equals(a.getStaffId()))
                .filter(a -> roomId == null || roomId.equals(a.getRoomId()))
                .toList();

        List<Patient> tenantPatients = patients.findByTenantId(tenantId);

        long newPatients = tenantPatients.stream()
                .filter(p -> between(p.getCreatedAt(), range.from(), range.toExclusive()))
                .count();

        Set<UUID> attendedPatientIds = selectedEncounters.stream()
                .map(Encounter::getPatientId)
                .collect(Collectors.toSet());

        long recurrentPatients = tenantPatients.stream()
                .filter(p -> attendedPatientIds.contains(p.getId()))
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isBefore(range.from()))
                .count();

        long completed = selectedAppointments.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
        long cancelled = selectedAppointments.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count();

        List<DashboardResponse.TimePoint> attentionsByPeriod = buildAttentionTimeline(selectedEncounters, range);
        List<DashboardResponse.TimePoint> patientsByMonth = buildPatientMonthlySeries(tenantPatients, range);

        List<DashboardResponse.NamedValue> byDentist = selectedEncounters.stream()
                .collect(Collectors.groupingBy(Encounter::getProfessionalStaffId, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new DashboardResponse.NamedValue(staffName(staffById.get(e.getKey())), e.getValue()))
                .sorted(Comparator.comparingLong(DashboardResponse.NamedValue::value).reversed())
                .limit(8)
                .toList();

        List<DashboardResponse.NamedValue> byRoom = selectedEncounters.stream()
                .filter(e -> e.getRoomId() != null)
                .collect(Collectors.groupingBy(Encounter::getRoomId, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new DashboardResponse.NamedValue(roomName(roomById.get(e.getKey())), e.getValue()))
                .sorted(Comparator.comparingLong(DashboardResponse.NamedValue::value).reversed())
                .limit(8)
                .toList();

        List<DashboardResponse.ComparisonPoint> attendedVsCancelled = buildAppointmentComparison(selectedAppointments, range);

        List<DashboardResponse.NamedValue> treatmentStats = selectedEncounters.stream()
                .map(Encounter::getProceduresSummary)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> new DashboardResponse.NamedValue(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(DashboardResponse.NamedValue::value).reversed())
                .limit(8)
                .toList();

        List<DashboardResponse.FilterOption> dentistOptions = tenantStaff.stream()
                .filter(Staff::isActive)
                .map(s -> new DashboardResponse.FilterOption(s.getId(), staffName(s)))
                .toList();

        List<DashboardResponse.FilterOption> roomOptions = tenantRooms.stream()
                .filter(Room::isActive)
                .map(r -> new DashboardResponse.FilterOption(r.getId(), r.getName()))
                .toList();

        return new DashboardResponse(
                range.label(),
                patients.countByTenantId(tenantId),
                selectedEncounters.size(),
                newPatients,
                completed,
                cancelled,
                tenantStaff.stream().filter(Staff::isActive).count(),
                false,
                BigDecimal.ZERO,
                attentionsByPeriod,
                patientsByMonth,
                byDentist,
                byRoom,
                attendedVsCancelled,
                new DashboardResponse.PatientMix(newPatients, recurrentPatients),
                treatmentStats,
                List.of(),
                dentistOptions,
                roomOptions
        );
    }

    private List<DashboardResponse.TimePoint> buildAttentionTimeline(List<Encounter> source, DateRange range) {
        Map<LocalDate, Long> grouped = source.stream().collect(Collectors.groupingBy(
                e -> e.getEncounteredAt().atZoneSameInstant(ZONE).toLocalDate(),
                TreeMap::new,
                Collectors.counting()
        ));

        List<DashboardResponse.TimePoint> result = new ArrayList<>();
        LocalDate start = range.from().atZoneSameInstant(ZONE).toLocalDate();
        LocalDate end = range.toExclusive().atZoneSameInstant(ZONE).toLocalDate().minusDays(1);
        long days = Duration.between(start.atStartOfDay(), end.plusDays(1).atStartOfDay()).toDays();

        if (days <= 45) {
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                result.add(new DashboardResponse.TimePoint(d.format(DAY_LABEL), grouped.getOrDefault(d, 0L)));
            }
            return result;
        }

        Map<YearMonth, Long> monthly = source.stream().collect(Collectors.groupingBy(
                e -> YearMonth.from(e.getEncounteredAt().atZoneSameInstant(ZONE)),
                TreeMap::new,
                Collectors.counting()
        ));
        YearMonth cursor = YearMonth.from(start);
        YearMonth finalMonth = YearMonth.from(end);
        while (!cursor.isAfter(finalMonth)) {
            result.add(new DashboardResponse.TimePoint(cursor.atDay(1).format(MONTH_LABEL), monthly.getOrDefault(cursor, 0L)));
            cursor = cursor.plusMonths(1);
        }
        return result;
    }

    private List<DashboardResponse.TimePoint> buildPatientMonthlySeries(List<Patient> source, DateRange range) {
        LocalDate selectedEnd = range.toExclusive().atZoneSameInstant(ZONE).toLocalDate().minusDays(1);
        YearMonth endMonth = YearMonth.from(selectedEnd);
        YearMonth startMonth = endMonth.minusMonths(5);

        Map<YearMonth, Long> grouped = source.stream()
                .filter(p -> p.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        p -> YearMonth.from(p.getCreatedAt().atZoneSameInstant(ZONE)),
                        TreeMap::new,
                        Collectors.counting()
                ));

        List<DashboardResponse.TimePoint> result = new ArrayList<>();
        for (YearMonth m = startMonth; !m.isAfter(endMonth); m = m.plusMonths(1)) {
            result.add(new DashboardResponse.TimePoint(m.atDay(1).format(MONTH_LABEL), grouped.getOrDefault(m, 0L)));
        }
        return result;
    }

    private List<DashboardResponse.ComparisonPoint> buildAppointmentComparison(List<Appointment> source, DateRange range) {
        Map<LocalDate, List<Appointment>> grouped = source.stream().collect(Collectors.groupingBy(
                a -> a.getStartsAt().atZoneSameInstant(ZONE).toLocalDate(),
                TreeMap::new,
                Collectors.toList()
        ));
        LocalDate start = range.from().atZoneSameInstant(ZONE).toLocalDate();
        LocalDate end = range.toExclusive().atZoneSameInstant(ZONE).toLocalDate().minusDays(1);
        long totalDays = Duration.between(start.atStartOfDay(), end.plusDays(1).atStartOfDay()).toDays();
        int step = totalDays > 31 ? Math.max(1, (int) Math.ceil(totalDays / 12.0)) : 1;

        List<DashboardResponse.ComparisonPoint> result = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(step)) {
            LocalDate bucketStart = d;
            LocalDate bucketEnd = d.plusDays(step);
            List<Appointment> bucket = grouped.entrySet().stream()
                    .filter(e -> !e.getKey().isBefore(bucketStart) && e.getKey().isBefore(bucketEnd))
                    .flatMap(e -> e.getValue().stream())
                    .toList();
            long attended = bucket.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count();
            long cancelled = bucket.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count();
            result.add(new DashboardResponse.ComparisonPoint(bucketStart.format(DAY_LABEL), attended, cancelled));
        }
        return result;
    }

    private DateRange resolveRange(String preset, LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now(ZONE);
        String normalized = preset == null ? "MONTH" : preset.trim().toUpperCase(Locale.ROOT);
        LocalDate start;
        LocalDate endInclusive;

        switch (normalized) {
            case "TODAY" -> {
                start = today;
                endInclusive = today;
            }
            case "WEEK" -> {
                start = today.with(DayOfWeek.MONDAY);
                endInclusive = start.plusDays(6);
            }
            case "CUSTOM" -> {
                if (from == null || to == null) throw new IllegalArgumentException("Para CUSTOM debe indicar from y to");
                if (to.isBefore(from)) throw new IllegalArgumentException("La fecha final no puede ser menor que la inicial");
                start = from;
                endInclusive = to;
            }
            default -> {
                start = today.with(TemporalAdjusters.firstDayOfMonth());
                endInclusive = today.with(TemporalAdjusters.lastDayOfMonth());
                normalized = "MONTH";
            }
        }

        OffsetDateTime startOffset = start.atStartOfDay(ZONE).toOffsetDateTime();
        OffsetDateTime endExclusive = endInclusive.plusDays(1).atStartOfDay(ZONE).toOffsetDateTime();
        return new DateRange(startOffset, endExclusive, normalized + " · " + start + " a " + endInclusive);
    }

    private boolean between(OffsetDateTime value, OffsetDateTime from, OffsetDateTime toExclusive) {
        return value != null && !value.isBefore(from) && value.isBefore(toExclusive);
    }

    private String staffName(Staff s) {
        if (s == null) return "Sin asignar";
        return (s.getFirstNames() + " " + s.getLastNames()).trim();
    }

    private String roomName(Room r) {
        return r == null ? "Sin consultorio" : r.getName();
    }

    private record DateRange(OffsetDateTime from, OffsetDateTime toExclusive, String label) {}
}
