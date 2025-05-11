package ru.mephi.abondarenko.otpapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mephi.abondarenko.otpapp.model.Role;
import ru.mephi.abondarenko.otpapp.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

    boolean existsByRole(Role role);
}
