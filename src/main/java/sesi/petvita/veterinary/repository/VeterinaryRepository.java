package sesi.petvita.veterinary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesi.petvita.veterinary.model.VeterinaryModel;

public interface VeterinaryRepository extends JpaRepository<VeterinaryModel,Long> {
}
