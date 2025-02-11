package br.com.projeto.service.exceptions;

public class UsuarioNaoEncontradoPeloId extends RuntimeException {
    public UsuarioNaoEncontradoPeloId(String message) {
        super(message);
    }
}
