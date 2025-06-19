package sesi.petvita.clinic.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sesi.petvita.clinic.model.ClinicModel;


@Repository
public interface ClinicRepository extends JpaRepository<ClinicModel, Long> {
}
