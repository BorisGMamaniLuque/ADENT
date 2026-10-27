package com.adent.reporting.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record DashboardResponse(
        String periodLabel,
        long patients,
        long encounters,
        long newPatients,
        long completedAppointments,
        long cancelledAppointments,
        long staff,
        boolean incomeAvailable,
        BigDecimal income,
        List<TimePoint> attentionsByPeriod,
        List<TimePoint> patientsByMonth,
        List<NamedValue> attentionsByDentist,
        List<NamedValue> attentionsByRoom,
        List<ComparisonPoint> attendedVsCancelled,
        PatientMix patientMix,
        List<NamedValue> treatments,
        List<MoneyPoint> incomeByMonth,
        List<FilterOption> dentists,
        List<FilterOption> rooms
) {
    public record TimePoint(String label, long value) {}
    public record NamedValue(String name, long value) {}
    public record ComparisonPoint(String label, long attended, long cancelled) {}
    public record PatientMix(long newPatients, long recurrentPatients) {}
    public record MoneyPoint(String label, BigDecimal value) {}
    public record FilterOption(UUID id, String name) {}
}
