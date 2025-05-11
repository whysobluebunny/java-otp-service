package ru.mephi.abondarenko.otpapp.dto;

import lombok.Data;

@Data
public class OtpConfigRequest {
    private int codeLength;
    private int expirationSeconds;
}
