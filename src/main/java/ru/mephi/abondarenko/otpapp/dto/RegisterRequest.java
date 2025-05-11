package ru.mephi.abondarenko.otpapp.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String login;
    private String password;
    private String role; // ADMIN / USER
}
