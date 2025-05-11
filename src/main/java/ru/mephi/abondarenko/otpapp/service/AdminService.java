package ru.mephi.abondarenko.otpapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mephi.abondarenko.otpapp.dto.OtpConfigRequest;
import ru.mephi.abondarenko.otpapp.model.OtpCode;
import ru.mephi.abondarenko.otpapp.model.OtpConfig;
import ru.mephi.abondarenko.otpapp.model.Role;
import ru.mephi.abondarenko.otpapp.model.User;
import ru.mephi.abondarenko.otpapp.repository.OtpCodeRepository;
import ru.mephi.abondarenko.otpapp.repository.OtpConfigRepository;
import ru.mephi.abondarenko.otpapp.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OtpConfigRepository otpConfigRepository;
    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;

    public void updateOtpConfig(OtpConfigRequest request) {
        OtpConfig config = otpConfigRepository.findAll().stream().findFirst()
                .orElse(OtpConfig.builder().build());

        config.setCodeLength(request.getCodeLength());
        config.setExpirationSeconds(request.getExpirationSeconds());

        otpConfigRepository.save(config);
    }

    public List<User> getAllUsersExcludingAdmins() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() != Role.ADMIN)
                .toList();
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        if (user.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя удалить администратора");
        }

        List<OtpCode> codes = otpCodeRepository.findAllByUser(user);
        otpCodeRepository.deleteAll(codes);

        userRepository.delete(user);
    }

}
