package com.igreja.agenda.controller;

import com.igreja.agenda.dto.AuthRequest;
import com.igreja.agenda.dto.AuthResponse;
import com.igreja.agenda.dto.ForgotPasswordRequest;
import com.igreja.agenda.dto.ResetPasswordRequest;
import com.igreja.agenda.service.AuthService;
import com.igreja.agenda.service.PasswordResetService;
import com.igreja.agenda.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        passwordResetService.requestPasswordReset(request.getEmail());
        return new ApiResponse<>(true, null, "Se o email estiver cadastrado, você receberá instruções de recuperação.");
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getSenha());
        return new ApiResponse<>(true, null, "Senha alterada com sucesso.");
    }
}