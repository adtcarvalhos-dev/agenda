package com.igreja.agenda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {

    @Email(message = "Email inválido")
    @NotBlank(message = "Email é obrigatório")
    private String email;
}
