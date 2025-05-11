package ru.mephi.abondarenko.otpapp.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mephi.abondarenko.otpapp.dto.OtpDeliveryRequest;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final EmailNotificationService emailService;
    private final TelegramNotificationService telegramService;
    private final SmsNotificationService smsService;
    private final FileNotificationService fileService;

    public void sendAll(String code, OtpDeliveryRequest request) {
        for (String channel : request.getChannels()) {
            switch (channel.toUpperCase()) {
                case "EMAIL" -> emailService.sendCode(request.getEmail(), code);
                case "TELEGRAM" -> telegramService.sendCode(request.getTelegramChatId(), code);
                case "SMS" -> smsService.sendCode(request.getPhoneNumber(), code);
                case "FILE" -> fileService.saveToFile(code);
            }
        }
    }
}
