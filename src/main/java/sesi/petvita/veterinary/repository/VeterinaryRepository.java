package sesi.petvita.veterinary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sesi.petvita.veterinary.model.VeterinaryModel;

@Repository
public interface VeterinaryRepository extends JpaRepository<VeterinaryModel,Long> {
}
