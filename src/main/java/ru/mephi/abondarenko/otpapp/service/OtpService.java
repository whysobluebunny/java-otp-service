package ru.mephi.abondarenko.otpapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mephi.abondarenko.otpapp.dto.OtpValidateRequest;
import ru.mephi.abondarenko.otpapp.model.OtpCode;
import ru.mephi.abondarenko.otpapp.model.OtpConfig;
import ru.mephi.abondarenko.otpapp.model.OtpStatus;
import ru.mephi.abondarenko.otpapp.model.User;
import ru.mephi.abondarenko.otpapp.repository.OtpCodeRepository;
import ru.mephi.abondarenko.otpapp.repository.OtpConfigRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final OtpConfigRepository otpConfigRepository;

    private final SecureRandom random = new SecureRandom();

    public String generateCode(User user, String operationId) {
        OtpConfig config = otpConfigRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("OTP config not set"));

        int length = config.getCodeLength();
        int expireSeconds = config.getExpirationSeconds();

        String code = generateNumericCode(length);

        OtpCode otp = OtpCode.builder()
                .user(user)
                .operationId(operationId)
                .code(code)
                .status(OtpStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(expireSeconds))
                .build();

        otpCodeRepository.save(otp);

        return code;
    }

    public boolean validateCode(User user, OtpValidateRequest request) {
        OtpCode otp = otpCodeRepository.findByUserAndOperationIdAndStatus(user, request.getOperationId(), OtpStatus.ACTIVE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Активный код не найден"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            otp.setStatus(OtpStatus.EXPIRED);
            otpCodeRepository.save(otp);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Код просрочен");
        }

        if (!otp.getCode().equals(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный код");
        }

        otp.setStatus(OtpStatus.USED);
        otpCodeRepository.save(otp);

        return true;
    }

    private String generateNumericCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 0-9
        }
        return sb.toString();
    }
}
