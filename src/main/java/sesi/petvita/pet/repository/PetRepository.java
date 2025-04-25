package sesi.petvita.pet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sesi.petvita.pet.model.PetModel;

@Repository
public interface PetRepository extends JpaRepository<PetModel,Long> {
}
