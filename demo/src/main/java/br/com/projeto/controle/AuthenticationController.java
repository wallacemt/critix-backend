package br.com.projeto.controle;

import br.com.projeto.dto.AutheticationDTO;
import br.com.projeto.dto.RegisterDTO;
import br.com.projeto.dto.ResetDTO;
import br.com.projeto.infra.security.TokenService;
import br.com.projeto.dto.EmailDTO;
import br.com.projeto.models.usuario.*;
import br.com.projeto.service.EmailService;
import br.com.projeto.repositorio.UsuarioRepository;
import br.com.projeto.service.UsuarioGerenciamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

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

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid AutheticationDTO data) {
        try {
            var usernameSenha = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
            var auth = this.authenticationManager.authenticate(usernameSenha);

            var token = tokenService.generateToken((Usuario) auth.getPrincipal());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login bem-sucedido.");
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Usuário ou senha inválidos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        // Verifica se o email já existe no banco de dados
        if (this.usuarioRepository.findByEmail(data.email()).isPresent()) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Email já registrado!");
            }});
        }

        // Verifica se as senhas coincidem
        if (!data.senha().equals(data.confirmacaoSenha())) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "As senhas não coincidem!");
            }});
        }

        // Criptografa a senha antes de salvar no banco
        String encryptedSenha = new BCryptPasswordEncoder().encode(data.senha());

        // Cria um novo usuário com os dados fornecidos
        Usuario newUsuario = new Usuario(
                null,
                data.nome(),
                data.email(),
                encryptedSenha,
                null, // imagePath (opcional)
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
        return ResponseEntity.ok().body(new HashMap<String, String>() {{
            put("message", "Usuário registrado com sucesso!");
        }});
    }


    //    Envia código de recuperação de senha para o Usuário pelo email
    @PostMapping("/recover")
    public ResponseEntity CodigoRecuperacaoSenha(@RequestParam("login") String email){
        if (this.usuarioRepository.findByEmail(email).isEmpty()){
            return ResponseEntity.ok().body(new HashMap<String, String>() {{
                put("error", "Usuário não Cadastrado!");
            }});
        }

        usuarioGerenciamentoService.solicitarCodigo(email);
        return ResponseEntity.ok().body(new HashMap<String, String>() {{
            put("message", "Código de recuperação de Senha enviado por email");
        }});
    }

    @PostMapping("/recover/code")
    public ResponseEntity<HashMap<String, String>> verificarCodigoRecuperacao(@RequestBody ResetDTO resetDTO) {
        // Verificar se o código foi enviado
        if (resetDTO.codigo() == null) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Código de recuperação não informado.");
            }});
        }

        // Buscar o usuário pelo código de recuperação
        Optional<Usuario> usuarioOptional = usuarioRepository.findByCodigoRecuperacaoSenha(resetDTO.codigo());

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Código inválido.");
            }});
        }

        // Código válido
        return ResponseEntity.ok(new HashMap<String, String>() {{
            put("sucess", "Código válido.");
        }}) ;
    }


    //Redefinição de senha com o código enviado por email do /recover para o usuário
    @PostMapping("/reset")
    public ResponseEntity reset(@RequestBody ResetDTO resetDTO){
        if (this.usuarioRepository.findByEmail(resetDTO.email()) == null){
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Usuário não cadastrado!");
            }});
        }
        if(!resetDTO.senha().equals(resetDTO.confirmacaoSenha())){
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "As senhas não coincidem!");
            }});
        }
        if(resetDTO.senha().length()<8){
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "A senha deve ter pelo menos 8 caracteres.");
            }});
        }

        //return ResponseEntity.ok().body(usuarioGerenciamentoService.alterarSenha(resetDTO));
        AlterarSenhaResponse response = usuarioGerenciamentoService.alterarSenha(resetDTO);
        if (response.isSucesso()) {
            return ResponseEntity.ok().body(new HashMap<String, String>() {{
                put("message", response.getMensagem());
            }});
        } else {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", response.getMensagem());
            }});
        }
    }

    @GetMapping("")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("{\"message\": \"essa e a rota de autenticação\"}");
    }

    // Teste de Autenticação Para ver o funcionamento do Token JWT
    @GetMapping("teste")
    public ResponseEntity testeAutenticacao(){
        return ResponseEntity.ok("Usuário Autenticado!");
    }
}
