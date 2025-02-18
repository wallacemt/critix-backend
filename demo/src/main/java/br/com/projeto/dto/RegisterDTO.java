package br.com.projeto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank @Size(max = 255) String nome,

        @NotBlank
        @Size(max = 255, message = "O email não pode ultrapassar 255 caracteres")
        @Email(message = "O email informado é inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres")
        String senha,


        @NotBlank(message = "A confirmação de senha é obrigatória.")
        String confirmacaoSenha
) {}
