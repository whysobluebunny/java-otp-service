package ru.mephi.abondarenko.otpapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mephi.abondarenko.otpapp.model.OtpConfig;

public interface OtpConfigRepository extends JpaRepository<OtpConfig, Long> {
}
