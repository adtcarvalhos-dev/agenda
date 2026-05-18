package com.igreja.agenda.repository;

import com.igreja.agenda.entity.ResetPasswordToken;
import com.igreja.agenda.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findByToken(String token);

    void deleteAllByUsuario(Usuario usuario);
}
