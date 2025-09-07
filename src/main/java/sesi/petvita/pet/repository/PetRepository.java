package sesi.petvita.pet.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sesi.petvita.pet.model.PetModel;
import sesi.petvita.user.model.UserModel;

import java.util.List;


@Repository
public interface PetRepository extends JpaRepository<PetModel,Long> {

    List<PetModel> findByUsuario(UserModel usuario);
}
