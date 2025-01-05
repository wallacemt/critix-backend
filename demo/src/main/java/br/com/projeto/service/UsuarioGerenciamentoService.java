package br.com.projeto.service;

import br.com.projeto.models.email.EmailDTO;
import br.com.projeto.models.usuario.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.projeto.repositorio.UsuarioRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioGerenciamentoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String solicitarCodigo(String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isEmpty()) {
            return "Usuário não encontrado!";
        }

        Usuario usuario = usuarioOptional.get();
        String codigoRecuperacao = gerarCodigoRecuperacao();
        usuario.setCodigoRecuperacaoSenha(codigoRecuperacao);
        usuario.setDataEnvioCodigo(new Date());

        usuarioRepository.saveAndFlush(usuario);

        EmailDTO emailDTO = new EmailDTO(
                usuario.getEmail(),
                "Código de Recuperação de Senha",
                "Seu código para recuperação de senha: " + codigoRecuperacao
        );
        emailService.enviarEmail(emailDTO);

        return "Código enviado!";
    }

    private String gerarCodigoRecuperacao() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public AlterarSenhaResponse alterarSenha(ResetDTO usuario) {
        Optional<Usuario> usuarioBanco = usuarioRepository.findByEmailAndCodigoRecuperacaoSenha(
                usuario.email(), usuario.codigo());

        if (usuarioBanco.isEmpty()) {
            return new AlterarSenhaResponse(false, "Email ou código não encontrados!");
        }

        Usuario usuarioEncontrado = usuarioBanco.get();
        Instant agora = Instant.now();
        Instant envioCodigo = usuarioEncontrado.getDataEnvioCodigo().toInstant();
        Duration diferenca = Duration.between(envioCodigo, agora);

        if (diferenca.toMinutes() > 15) {
            return new AlterarSenhaResponse(false, "Tempo expirado, solicite um novo código!");
        }

        String novaSenhaCriptografada = passwordEncoder.encode(usuario.senha());
        usuarioEncontrado.setSenha(novaSenhaCriptografada);
        usuarioEncontrado.setCodigoRecuperacaoSenha(null);
        usuarioRepository.saveAndFlush(usuarioEncontrado);

        return new AlterarSenhaResponse(true, "Senha alterada com sucesso!");
    }
}

