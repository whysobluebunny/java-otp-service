package ru.mephi.abondarenko.otpapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class OtpDeliveryRequest {
    private String operationId;
    private List<String> channels;
    private String email;
    private String telegramChatId;
    private String phoneNumber;
}
