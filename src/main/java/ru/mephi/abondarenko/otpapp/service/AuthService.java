package ru.mephi.abondarenko.otpapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mephi.abondarenko.otpapp.dto.LoginRequest;
import ru.mephi.abondarenko.otpapp.dto.RegisterRequest;
import ru.mephi.abondarenko.otpapp.dto.TokenResponse;
import ru.mephi.abondarenko.otpapp.model.Role;
import ru.mephi.abondarenko.otpapp.model.User;
import ru.mephi.abondarenko.otpapp.repository.UserRepository;
import ru.mephi.abondarenko.otpapp.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        if (request.getRole().equalsIgnoreCase("ADMIN") &&
                userRepository.existsByRole(Role.valueOf("ADMIN"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Администратор уже существует");
        }

        if (userRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует");
        }

        User user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .build();

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль");
        }

        String token = jwtUtil.generateToken(user.getLogin(), user.getRole().name());
        return new TokenResponse(token);
    }
}
