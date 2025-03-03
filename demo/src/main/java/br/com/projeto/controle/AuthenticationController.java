package br.com.projeto.controle;

import br.com.projeto.dto.*;
import br.com.projeto.infra.security.TokenService;
import br.com.projeto.service.AuthenticationService;
import br.com.projeto.service.RefreshTokenService;
import br.com.projeto.ultils.AccountDisabledException;
import br.com.projeto.ultils.EmailAlreadyRegisteredException;
import br.com.projeto.ultils.PasswordsDoNotMatchException;
import jakarta.validation.Valid;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AutheticationDTO data) {
        try {
            // Tenta fazer o login
            LoginResponseDTO response = authenticationService.login(data);
            return ResponseEntity.ok(response); // Retorna a resposta de sucesso com status 200 OK
        } catch (InvalidCredentialsException e) {
            // Erro de credenciais inválidas
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Invalid Credentials", e.getMessage(), "Credenciais incorretas.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse); // Status 401

        } catch (AccountDisabledException e) {
            // Conta desativada
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Account Disabled", e.getMessage(), "Sua conta foi desativada.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse); // Status 403
        } catch (Exception e) {
            // Erro genérico
            ErrorResponseDTO errorResponse = new ErrorResponseDTO("Internal Server Error", e.getMessage(), "Ocorreu um erro inesperado.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // Status 500
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegisterDTO data) {
        try {
            Map<String, String> response = authenticationService.register(data);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyRegisteredException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Email já registrado!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (PasswordsDoNotMatchException e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "As senhas não coincidem!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Ocorreu um erro ao tentar registrar o usuário.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/google")
    public Map<String, String> loginWithGoogle(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        return authenticationService.loginWithGoogle(idToken);
    }

    @PostMapping("/recover")
    public ResponseEntity<Map<String, String>> recover(@RequestParam("login") String email) {
        return ResponseEntity.ok(authenticationService.recover(email));
    }

    @PostMapping("/recover/code")
    public ResponseEntity<Map<String, String>> verificarCodigoRecuperacao(@RequestBody ResetDTO resetDTO) {
        return ResponseEntity.ok(authenticationService.verifyRecoveryCode(resetDTO));
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, String>> reset(@RequestBody ResetDTO resetDTO) {
        return ResponseEntity.ok(authenticationService.resetPassword(resetDTO));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "RefreshToken é obrigatório"));
        }

        String newAcessToken = refreshTokenService.createAcessTokenFromRefreshToken(refreshToken);

        return ResponseEntity.ok(Map.of("acessToken", newAcessToken));
    }

    @GetMapping("")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("{\"message\": \"essa e a rota de autenticação\"}");
    }
}
