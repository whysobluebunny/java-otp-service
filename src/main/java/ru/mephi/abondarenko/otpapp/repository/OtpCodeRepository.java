package ru.mephi.abondarenko.otpapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mephi.abondarenko.otpapp.model.OtpCode;
import ru.mephi.abondarenko.otpapp.model.OtpStatus;
import ru.mephi.abondarenko.otpapp.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByUserAndOperationIdAndStatus(User user, String operationId, OtpStatus status);

    List<OtpCode> findAllByStatusAndExpiresAtBefore(OtpStatus status, LocalDateTime time);

    List<OtpCode> findAllByUser(User user);
}
