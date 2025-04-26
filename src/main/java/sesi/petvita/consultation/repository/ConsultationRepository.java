package sesi.petvita.consultation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesi.petvita.consultation.model.ConsultationModel;

public interface ConsultationRepository extends JpaRepository<ConsultationModel, Long> {
}
