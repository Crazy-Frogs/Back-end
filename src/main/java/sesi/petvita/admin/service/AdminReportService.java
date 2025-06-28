package sesi.petvita.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sesi.petvita.admin.dto.MonthlyReportDTO;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.consultation.repository.ConsultationRepository;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ConsultationRepository consultationRepository;

    public MonthlyReportDTO getMonthlySummary(int year, int month, Optional<Long> vetId, Optional<SpecialityEnum> speciality) {
        YearMonth yearMonth = YearMonth.of(year, month);

        // Simplesmente buscando todas as consultas por enquanto.
        // Em um cenário real, você otimizaria essa query para filtrar por data, vet e especialidade no banco.
        List<ConsultationModel> allConsultations = consultationRepository.findAll();

        List<ConsultationModel> filteredConsultations = allConsultations.stream()
                .filter(c -> c.getConsultationdate().getYear() == year && c.getConsultationdate().getMonthValue() == month)
                .filter(c -> vetId.map(id -> c.getVeterinario().getId().equals(id)).orElse(true))
                .filter(c -> speciality.map(s -> c.getSpecialityEnum().equals(s)).orElse(true))
                .collect(Collectors.toList());

        long total = filteredConsultations.size();

        Map<String, Long> byStatus = filteredConsultations.stream()
                .collect(Collectors.groupingBy(c -> c.getStatus().getDescricao(), Collectors.counting()));

        Map<String, Long> bySpeciality = filteredConsultations.stream()
                .collect(Collectors.groupingBy(c -> c.getSpecialityEnum().getDescricao(), Collectors.counting()));

        return new MonthlyReportDTO(year, month, total, byStatus, bySpeciality);
    }
}