package ru.mephi.abondarenko.otpapp.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.mephi.abondarenko.otpapp.model.OtpCode;
import ru.mephi.abondarenko.otpapp.model.OtpStatus;
import ru.mephi.abondarenko.otpapp.repository.OtpCodeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpExpirationScheduler {

    private final OtpCodeRepository otpCodeRepository;

    @Scheduled(fixedRate = 60_000) // каждые 60 секунд
    public void expireOldCodes() {
        List<OtpCode> expired = otpCodeRepository
                .findAllByStatusAndExpiresAtBefore(OtpStatus.ACTIVE, LocalDateTime.now());

        for (OtpCode code : expired) {
            code.setStatus(OtpStatus.EXPIRED);
        }

        if (!expired.isEmpty()) {
            otpCodeRepository.saveAll(expired);
            log.info("Expired {} OTP codes", expired.size());
        }
    }
}
