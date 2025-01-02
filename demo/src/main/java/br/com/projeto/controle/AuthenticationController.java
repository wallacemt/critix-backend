package br.com.projeto.controle;

import br.com.projeto.infra.security.DadosTokenJWT;
import br.com.projeto.infra.security.TokenService;
import br.com.projeto.models.email.EmailDTO;
import br.com.projeto.models.usuario.*;
import br.com.projeto.service.EmailService;
import br.com.projeto.service.UsuarioGerenciamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioGerenciamentoService usuarioGerenciamentoService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AutheticationDTO data){
        var usernameSenha = new UsernamePasswordAuthenticationToken(data.login(),data.senha());
        //Autentica se o nosso usuário existe no banco
        var auth = this.authenticationManager.authenticate(usernameSenha);  //Acessa a classe AuthorizationService para conferir se o login e senha existem no banco de dados

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new DadosTokenJWT(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if (this.usuarioRepository.findByLogin(data.login()) != null){
            return ResponseEntity.badRequest().body("Usuário já existe!");
        }

        if(!data.senha().equals(data.confirmacaoSenha())){
            return  ResponseEntity.badRequest().body("As senhas não conicidem!");
        }

        String encryptedSenha = new BCryptPasswordEncoder().encode(data.senha());
        Usuario newUsuario = new Usuario(data.login(),encryptedSenha, data.nome())  ;

        this.usuarioRepository.save(newUsuario);
        var emailDTO = new EmailDTO(data.login(),"Cadastro na Critix","Registro no site Critix realizado com sucesso!");
        emailService.enviarEmail(emailDTO);
        return ResponseEntity.ok().body("Usuário registrado com sucesso!");
    }

    //Envia código de recuperação de senha para o Usuário pelo email
    @PostMapping("/recover")
    public ResponseEntity CodigoRecuperacaoSenha(@RequestParam("login") String email){
        if (this.usuarioRepository.buscarPorLogin(email) == null){
            return ResponseEntity.badRequest().body("Usuário não cadastrado!");
        }

        usuarioGerenciamentoService.solicitarCodigo(email);
        return ResponseEntity.ok().body("Código de recuperação de Senha enviado por email");

    }

    //Redefinição de senha com o código enviado por email do /recover para o usuário
    @PostMapping("/reset")
    public ResponseEntity reset(@RequestBody ResetDTO resetDTO){
        if (this.usuarioRepository.buscarPorLogin(resetDTO.email()) == null){
            return ResponseEntity.badRequest().body("Usuário não cadastrado!");
        }
        if(!resetDTO.senha().equals(resetDTO.confirmacaoSenha())){
            return ResponseEntity.badRequest().body("As senhas não conicidem!");
        }
        if(resetDTO.senha().length()<8){
            return ResponseEntity.badRequest().body("A senha deve ter pelo menos 8 caracteres");
        }

//        return ResponseEntity.ok().body(usuarioGerenciamentoService.alterarSenha(resetDTO));
        AlterarSenhaResponse reponse = usuarioGerenciamentoService.alterarSenha(resetDTO);
        return ResponseEntity.status(reponse.isSucesso() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(reponse);
    }

    @GetMapping("")
    public String hello(){
        return "Olá mundo";
    }

}
