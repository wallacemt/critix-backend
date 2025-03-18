package br.com.projeto.models.usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlterarSenhaResponse {
    private boolean sucesso;
    private String mensagem;
}
