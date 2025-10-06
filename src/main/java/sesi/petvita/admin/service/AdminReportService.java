package sesi.petvita.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesi.petvita.admin.dto.ReportSummaryDTO;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ConsultationRepository consultationRepository;

    // REATORADO: Este método agora usa a query otimizada do repositório
    public ReportSummaryDTO getSummaryByDateRange(
            LocalDate startDate, LocalDate endDate, Optional<Long> vetId, Optional<SpecialityEnum> speciality) {

        List<ConsultationModel> filteredConsultations = consultationRepository.findWithFilters(
                startDate, endDate, vetId.orElse(null), speciality.orElse(null)
        );

        long total = filteredConsultations.size();

        Map<String, Long> byStatus = filteredConsultations.stream()
                .collect(Collectors.groupingBy(c -> c.getStatus().getDescricao(), Collectors.counting()));

        Map<String, Long> bySpeciality = filteredConsultations.stream()
                .collect(Collectors.groupingBy(c -> c.getSpecialityEnum().getDescricao(), Collectors.counting()));

        return new ReportSummaryDTO(total, byStatus, bySpeciality);
    }
}