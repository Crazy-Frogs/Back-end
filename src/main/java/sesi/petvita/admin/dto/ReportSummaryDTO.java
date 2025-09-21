package sesi.petvita.admin.dto;

import java.util.Map;

// NOVO: DTO mais genérico para relatórios por período
public record ReportSummaryDTO(
        long totalConsultations,
        Map<String, Long> consultationsByStatus,
        Map<String, Long> consultationsBySpeciality
) {}