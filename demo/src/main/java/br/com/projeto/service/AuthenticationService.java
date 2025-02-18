package br.com.projeto.service;

import br.com.projeto.dto.AutheticationDTO;
import br.com.projeto.dto.RegisterDTO;
import br.com.projeto.dto.ResetDTO;
import br.com.projeto.infra.security.TokenService;
import br.com.projeto.dto.EmailDTO;
import br.com.projeto.models.usuario.AlterarSenhaResponse;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.UsuarioRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Value("${google.client.id}")
    private String googleClientId;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioGerenciamentoService usuarioGerenciamentoService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Map<String, String> loginWithGoogle(String idToken) {
        try {
            // Verifica se o token e valido
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            String response = restTemplate.getForObject(url, String.class);

            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            String email = jsonResponse.get("email").getAsString();
            String nome = jsonResponse.get("name").getAsString();

            Optional<Usuario> existingUser = usuarioRepository.findByEmail(email);
            if(existingUser.isPresent()){
                Usuario usuario = existingUser.get();
                String token = tokenService.generateToken(usuario);
                String refreshToken = tokenService.generateRefreshToken(usuario);

                Map<String, String>  responseMap = new HashMap<>();
                responseMap.put("message", "Bem-Vindo " + usuario.getNome());
                responseMap.put("token", token);
                responseMap.put("refreshToken", refreshToken);
                return responseMap;
            }

            // Usuario não cadastrado, cria um novo usuario.
            String senhaTemp = "temp_password_" + email;
            String encryptedPassword = passwordEncoder.encode(senhaTemp);

            Usuario newUser = new Usuario(null, nome, email, encryptedPassword, null, null, 0,0,0);
            usuarioRepository.save(newUser);

            //Gera o token para o novo usuario
            String token = tokenService.generateToken(newUser);
            String refreshToken = tokenService.generateRefreshToken(newUser);
            Map<String, String>  responseMap = new HashMap<>();
            responseMap.put("message", "Usuário registrado com sucesso!");
            responseMap.put("token",token);
            responseMap.put("refreshToken", refreshToken);
            return  responseMap;
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao processar a autenticação do Google");
            return  errorResponse;
        }
    }

    public Map<String, String> login(AutheticationDTO data) {
        try {
            var usernameSenha = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
            var auth = this.authenticationManager.authenticate(usernameSenha);

            Usuario usuario = (Usuario) auth.getPrincipal();
            String acessToken = tokenService.generateToken(usuario);
            String refreshToken = tokenService.generateRefreshToken(usuario);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Bem-vindo, " + ((Usuario) auth.getPrincipal()).getNome());
            response.put("token", acessToken);
            response.put("refreshToken", refreshToken);
            return response;
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Usuário ou senha inválidos.");
            return errorResponse;
        }
    }

    public Map<String, String> register(RegisterDTO data) {
        // Verifica se o email já existe no banco de dados
        if (this.usuarioRepository.findByEmail(data.email()).isPresent()) {
            return new HashMap<String, String>() {{
                put("error", "Email já registrado!");
            }};
        }

        // Verifica se as senhas coincidem
        if (!data.senha().equals(data.confirmacaoSenha())) {
            return new HashMap<String, String>() {{
                put("error", "As senhas não coincidem!");
            }};
        }

        // Criptografa a senha antes de salvar no banco
        String encryptedSenha = passwordEncoder.encode(data.senha());

        // Cria um novo usuário com os dados fornecidos
        Usuario newUsuario = new Usuario(
                null,
                data.nome(),
                data.email(),
                encryptedSenha,
                null,
                null,
                0, // reviews
                0, // followers
                0  // followings
        );

        // Salva o novo usuário no banco de dados
        this.usuarioRepository.save(newUsuario);

        // Envia um email de confirmação de registro
        var emailDTO = new EmailDTO(data.email(), "Cadastro na Critix", "Registro no site Critix realizado com sucesso!");
        emailService.enviarEmail(emailDTO);

        // Retorna uma resposta de sucesso
        return new HashMap<String, String>() {{
            put("message", "Usuário registrado com sucesso!");
        }};
    }

    public Map<String, String> recover(String email) {
        if (this.usuarioRepository.findByEmail(email).isEmpty()){
            return new HashMap<String, String>() {{
                put("error", "Usuário não Cadastrado!");
            }};
        }

        usuarioGerenciamentoService.solicitarCodigo(email);
        return new HashMap<String, String>() {{
            put("message", "Código de recuperação de Senha enviado por email");
        }};
    }

    public Map<String, String> verifyRecoveryCode(ResetDTO resetDTO) {
        // Verificar se o código foi enviado
        if (resetDTO.codigo() == null) {
            return new HashMap<String, String>() {{
                put("error", "Código de recuperação não informado.");
            }};
        }

        // Buscar o usuário pelo código de recuperação
        Optional<Usuario> usuarioOptional = usuarioRepository.findByCodigoRecuperacaoSenha(resetDTO.codigo());

        if (usuarioOptional.isEmpty()) {
            return new HashMap<String, String>() {{
                put("error", "Código inválido.");
            }};
        }

        // Código válido
        return new HashMap<String, String>() {{
            put("sucess", "Código válido.");
        }};
    }

    public Map<String, String> resetPassword(ResetDTO resetDTO) {
        if (this.usuarioRepository.findByEmail(resetDTO.email()) == null){
            return new HashMap<String, String>() {{
                put("error", "Usuário não cadastrado!");
            }};
        }
        if(!resetDTO.senha().equals(resetDTO.confirmacaoSenha())){
            return new HashMap<String, String>() {{
                put("error", "As senhas não coincidem!");
            }};
        }
        if(resetDTO.senha().length()<8){
            return new HashMap<String, String>() {{
                put("error", "A senha deve ter pelo menos 8 caracteres.");
            }};
        }

        AlterarSenhaResponse response = usuarioGerenciamentoService.alterarSenha(resetDTO);
        if (response.isSucesso()) {
            return new HashMap<String, String>() {{
                put("message", response.getMensagem());
            }};
        } else {
            return new HashMap<String, String>() {{
                put("error", response.getMensagem());
            }};
        }
    }
}
