package br.com.projeto.models.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank @Size(max = 255) String nome,

        @NotBlank @Email(message = "O email deve ser válido")
        @Size(max = 255, message = "O email não pode ultrapassar 255 caracteres")
        String email,

        @NotBlank @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
        String senha,

        @NotBlank String confirmacaoSenha
) {
}
