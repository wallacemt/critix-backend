package br.com.projeto.service;

import br.com.projeto.dto.ResetDTO;
import br.com.projeto.dto.EmailDTO;
import br.com.projeto.dto.UsuarioDTO;
import br.com.projeto.models.usuario.*;
import br.com.projeto.service.exceptions.UsuarioNaoEncontradoPeloId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.projeto.repositorio.UsuarioRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class UsuarioGerenciamentoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public UsuarioDTO getUser(String subject) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(subject);
        Usuario usuarioEntity = usuario.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para o e-mail: " + subject));

        return new UsuarioDTO(usuarioEntity);
    }

    public String setProfilePath(String subject, String urlPath){
        Optional<Usuario> usuario = usuarioRepository.findByEmail(subject);
        Usuario entityUser = usuario.orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado para o subject: " + subject));
        entityUser.setImagePath(urlPath);
        usuarioRepository.saveAndFlush(entityUser);
        return "Imagem Atualizada";
    }

    public String setBannerProfilePath(String subject, String bannerPath){
        Optional<Usuario> usuario = usuarioRepository.findByEmail(subject);
        Usuario entityUser = usuario.orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado para o subject: " + subject));
        entityUser.setBannerPath(bannerPath);
        usuarioRepository.saveAndFlush(entityUser);
        return "Banner Atualizado";
    }



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
        StringBuilder corpoEmail = new StringBuilder();


        EmailDTO emailDTO = new EmailDTO(
                usuario.getEmail(),
                "Solicitação de Recuperação de Senha - Critix   ",
                "Seu código para recuperação de senha: " + codigoRecuperacao
        );
        emailService.enviarEmail(emailDTO);

        return "Código enviado!";
    }



    private String gerarCodigoRecuperacao() {
        return String.format("%06d", (int) (Math.random() * 1000000));
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

    // Retorna as informações do usuário pelo ID
    public UsuarioDTO getUserById(Long id, Usuario usuario){
        Optional<Usuario> usuarioEntity = usuarioRepository.findById(id);

        if (usuarioEntity.isEmpty()){
            throw new UsuarioNaoEncontradoPeloId("Usuário não encontrado para o ID: "+id);
        }

        Usuario usuarioEncontrado = usuarioEntity.get();

        return new UsuarioDTO(usuarioEncontrado,true);
    }
}

