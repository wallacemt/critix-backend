package br.com.projeto.service;

import br.com.projeto.dto.*;
import br.com.projeto.infra.security.TokenService;
import br.com.projeto.models.usuario.*;
import br.com.projeto.repositorio.FollowerRepository;
import br.com.projeto.ultils.PasswordsDoNotMatchException;
import jakarta.transaction.Transactional;
import org.apache.http.auth.InvalidCredentialsException;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.com.projeto.repositorio.UsuarioRepository;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioGerenciamentoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Autowired
    private FollowerRepository followerRepository;

    public UsuarioDTO getUser(String subject) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(subject);
        Usuario usuarioEntity = usuario.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para o e-mail: " + subject));
        usuarioEntity.setFollowings(followerRepository.countFollowing(usuarioEntity.getId()));
        usuarioEntity.setFollowers(followerRepository.countFollowers(usuarioEntity.getId()));


        usuarioRepository.save(usuarioEntity);
        return new UsuarioDTO(usuarioEntity, false, true);
    }

    public String setProfilePath(String subject, String urlPath) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(subject);
        Usuario entityUser = usuario.orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado para o subject: " + subject));
        entityUser.setImagePath(urlPath);
        usuarioRepository.saveAndFlush(entityUser);
        return "Imagem Atualizada";
    }

    public String setBannerProfilePath(String subject, String bannerPath) {
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
    public UsuarioDTO getUserById(String username, Usuario usuario) {
        Optional<Usuario> usuarioEntity = usuarioRepository.findByUsernameUser(username);
        if (usuarioEntity.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado para o username: " + username);
        }
        Usuario usuarioEncontrado = usuarioEntity.get();
        usuarioEncontrado.setFollowings(followerRepository.countFollowing(usuarioEncontrado.getId()));
        usuarioEncontrado.setFollowers(followerRepository.countFollowers(usuarioEncontrado.getId()));

        usuarioRepository.save(usuarioEncontrado);

        boolean isUser = false;
        if (usuarioEntity.get().getId() == usuario.getId()) {
            isUser = true;
        }
        return new UsuarioDTO(usuarioEncontrado, true, isUser);
    }

    //Edita informações do usuario autenticado!
    @Transactional
    public UserEditResponseDTO editUserInfo(Map<String, Object> updates, Usuario usuario) {
        boolean hasChanges = false;
        boolean requiresReauthentication = false;

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String campo = entry.getKey();
            Object valor = entry.getValue();
            switch (campo) {
                case "name":
                    String novoNome = (String) valor;
                    if (!novoNome.equals(usuario.getNome()) && validName(novoNome)) {
                        usuario.setNome(novoNome);
                        hasChanges = true;
                    }
                    break;
                case "username":
                    String novoUsername = (String) valor;
                    Optional<Usuario> userFind = usuarioRepository.findByUsernameUser(novoUsername);
                    if(userFind.isPresent() && !usuario.getUsernameUser().equals(novoUsername)){
                        throw new DuplicateKeyException("Username já em uso");
                    }
                    if (!novoUsername.equals(usuario.getUsernameUser()) && validName(novoUsername)) {
                        usuario.setUsernameUser(novoUsername);
                        hasChanges = true;
                    }
                    break;
                case "email":
                    String novoEmail = (String) valor;
                    if (!novoEmail.equals(usuario.getEmail()) && validEmail(novoEmail)) {
                        usuario.setEmail(novoEmail);
                        requiresReauthentication = true;
                        hasChanges = true;
                    }
                    break;

                case "senha":
                    Map<String, String> senhaData = (Map<String, String>) valor;
                    String novaSenha = senhaData.get("senha");
                    String confirmacaoSenha = senhaData.get("confirmacaoSenha");

                    if (novaSenha != null && novaSenha.length() >= 8 && novaSenha.equals(confirmacaoSenha)) {
                        usuario.setSenha(passwordEncoder.encode(novaSenha));
                        requiresReauthentication = true;
                        hasChanges = true;
                    } else {
                        throw new PasswordsDoNotMatchException("As senhas não coincidem ou não possuem o mínimo de 8 caracteres!");
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Campo inválido: " + campo);
            }
        }

        if (hasChanges) {
            usuarioRepository.save(usuario);
        }
        String acessToken = tokenService.generateToken(usuario);
        String refreshToken = tokenService.generateRefreshToken(usuario);

        return new UserEditResponseDTO(usuario.getId(), usuario.getNome(), usuario.getUsernameUser(), usuario.getUsername(), acessToken, refreshToken);
    }

    private boolean validName(String nome) {
        return nome.length() >= 3 && nome.length() <= 50;
    }

    private boolean validEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$") &&
                usuarioRepository.findByEmail(email).isEmpty(); // Garante que o email seja único
    }

    @Transactional
    public void deleteUser(Usuario usuario, String password) throws InvalidCredentialsException {
        if(!passwordEncoder.matches(password, usuario.getPassword())){
            throw new InvalidCredentialsException("Senha Incorreta!");
        }
        usuarioRepository.delete(usuario);
    }
}

