package sesi.petvita.consultation.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sesi.petvita.consultation.model.ConsultationModel;
import sesi.petvita.veterinary.speciality.SpecialityEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<ConsultationModel, Long> {

    List<ConsultationModel> findByUsuarioId(Long usuarioId);

    List<ConsultationModel> findByVeterinarioIdAndConsultationdateAndConsultationtimeBetween(
            Long veterinarioId, LocalDate date, LocalTime startTime, LocalTime endTime
    );

    List<ConsultationModel> findByVeterinarioId(Long veterinarioId);

    List<ConsultationModel> findByVeterinarioIdAndSpecialityEnum(Long veterinarioId, SpecialityEnum specialityEnum);

    List<ConsultationModel> findByConsultationdate(LocalDate date);

    List<ConsultationModel> findBySpecialityEnum(SpecialityEnum speciality);

    List<ConsultationModel> findByVeterinario_NameContainingIgnoreCase(String veterinaryName);

    List<ConsultationModel> findByPet_NameContainingIgnoreCase(String petName);

    boolean existsByVeterinarioIdAndConsultationdateAndConsultationtime(Long veterinarioId, LocalDate date, LocalTime time);
}