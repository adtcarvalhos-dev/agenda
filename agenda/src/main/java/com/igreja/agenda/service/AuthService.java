package com.igreja.agenda.service;

import com.igreja.agenda.dto.AuthRequest;
import com.igreja.agenda.dto.AuthResponse;
import com.igreja.agenda.dto.LoginRequest;
import com.igreja.agenda.entity.Role;
import com.igreja.agenda.entity.Usuario;
import com.igreja.agenda.exception.BusinessException;
import com.igreja.agenda.repository.UsuarioRepository;
import com.igreja.agenda.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // REGISTER (CADASTRO)
    public AuthResponse register(AuthRequest request) {

        String email = request.getEmail().toLowerCase().trim();

        // verifica duplicidade corretamente
        if (repository.findByEmail(email).isPresent()) {
            throw new BusinessException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(Role.MEMBRO); // padrão seguro

        Usuario salvo = repository.save(usuario);

        String token = jwtService.gerarToken(salvo);

        return new AuthResponse(token);
    }

    // LOGIN
    public AuthResponse login(LoginRequest request) {

        String email = request.getEmail().toLowerCase().trim();

        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.gerarToken(usuario);

        return new AuthResponse(token);
    }
}
