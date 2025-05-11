package ru.mephi.abondarenko.otpapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.abondarenko.otpapp.dto.OtpDeliveryRequest;
import ru.mephi.abondarenko.otpapp.dto.OtpGenerateRequest;
import ru.mephi.abondarenko.otpapp.dto.OtpValidateRequest;
import ru.mephi.abondarenko.otpapp.model.User;
import ru.mephi.abondarenko.otpapp.repository.UserRepository;
import ru.mephi.abondarenko.otpapp.service.OtpService;
import ru.mephi.abondarenko.otpapp.service.notification.NotificationDispatcher;
import ru.mephi.abondarenko.otpapp.util.AuthUtil;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class OtpController {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final NotificationDispatcher dispatcher;

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody OtpGenerateRequest request) {
        User user = userRepository.findByLogin(AuthUtil.getCurrentUsername())
                .orElseThrow();

        String code = otpService.generateCode(user, request.getOperationId());
        return ResponseEntity.ok(code); // временно возвращаем прямо в ответ
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validate(@RequestBody OtpValidateRequest request) {
        User user = userRepository.findByLogin(AuthUtil.getCurrentUsername())
                .orElseThrow();

        otpService.validateCode(user, request);
        return ResponseEntity.ok("Код успешно подтвержден");
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestBody OtpDeliveryRequest request) {
        User user = userRepository.findByLogin(AuthUtil.getCurrentUsername())
                .orElseThrow();

        String code = otpService.generateCode(user, request.getOperationId());
        dispatcher.sendAll(code, request);

        return ResponseEntity.ok("OTP отправлен через каналы: " + request.getChannels());
    }

}
