package ru.mephi.abondarenko.otpapp.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Service
public class FileNotificationService {
    public void saveToFile(String code) {
        try (PrintWriter out = new PrintWriter(new FileWriter("otp_code.txt", true))) {
            out.println(code);
        } catch (IOException e) {
            log.error("Ошибка записи в файл: {}", e.getMessage());
        }
    }
}
