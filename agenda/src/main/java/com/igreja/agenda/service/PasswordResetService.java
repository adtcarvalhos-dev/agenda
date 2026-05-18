package com.igreja.agenda.service;

import com.igreja.agenda.entity.ResetPasswordToken;
import com.igreja.agenda.entity.Usuario;
import com.igreja.agenda.exception.BusinessException;
import com.igreja.agenda.repository.ResetPasswordTokenRepository;
import com.igreja.agenda.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final ResetPasswordTokenRepository tokenRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${app.reset-password-url:http://localhost:3000/reset-password}")
    private String resetPasswordUrl;

    @Value("${app.mail.from:no-reply@agenda.com}")
    private String mailFrom;

    @Transactional
    public void requestPasswordReset(String email) {
        String normalizedEmail = email.toLowerCase().trim();

        usuarioRepository.findByEmail(normalizedEmail).ifPresent(usuario -> {
            tokenRepository.deleteAllByUsuario(usuario);

            ResetPasswordToken token = new ResetPasswordToken();
            token.setToken(UUID.randomUUID().toString());
            token.setUsuario(usuario);
            token.setExpiracao(LocalDateTime.now().plusHours(2));
            tokenRepository.save(token);

            JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
            if (mailSender == null || mailHost == null || mailHost.isBlank()) {
                throw new BusinessException("Envio de email não configurado");
            }

            sendResetEmail(mailSender, usuario.getEmail(), token.getToken());
        });
    }

    @Transactional
    public void resetPassword(String tokenValue, String novaSenha) {
        ResetPasswordToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException("Token inválido"));

        if (token.getExpiracao().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(token);
            throw new BusinessException("Token expirado");
        }

        Usuario usuario = token.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        tokenRepository.delete(token);
    }

    private void sendResetEmail(JavaMailSender mailSender, String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(email);
        message.setSubject("Redefinição de senha - Agenda");

        if (resetPasswordUrl == null || resetPasswordUrl.isBlank()) {
            message.setText("Use este token para redefinir sua senha: " + token + "\n" +
                    "Envie-o em um POST para /auth/reset-password.");
        } else {
            message.setText("Para redefinir sua senha, acesse:\n" + resetPasswordUrl + "?token=" + token + "\n" +
                    "Caso não consiga abrir o link, use o token acima em /auth/reset-password.");
        }

        mailSender.send(message);
    }
}
