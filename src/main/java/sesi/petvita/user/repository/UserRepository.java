package sesi.petvita.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sesi.petvita.user.model.UserModel;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, Long> {
}
