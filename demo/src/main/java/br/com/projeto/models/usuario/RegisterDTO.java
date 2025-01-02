package br.com.projeto.models.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank String login,
        @NotBlank @Size(min = 8,message = "A senha deve ter pelo menos 8 caracteres") String senha,
        @NotBlank String nome,
        @NotBlank String confirmacaoSenha) {
}
