package sesi.petvita.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesi.petvita.pet.model.PetModel;

public interface PetRepository extends JpaRepository<PetModel,Long> {
}
