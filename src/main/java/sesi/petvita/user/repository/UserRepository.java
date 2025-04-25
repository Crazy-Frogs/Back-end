package sesi.petvita.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sesi.petvita.user.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
}
