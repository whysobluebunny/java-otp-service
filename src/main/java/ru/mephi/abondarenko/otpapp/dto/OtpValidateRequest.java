package ru.mephi.abondarenko.otpapp.dto;

import lombok.Data;

@Data
public class OtpValidateRequest {
    private String operationId;
    private String code;
}
