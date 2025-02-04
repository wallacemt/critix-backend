package br.com.projeto.controle;

import br.com.projeto.dto.AutheticationDTO;
import br.com.projeto.dto.RegisterDTO;
import br.com.projeto.dto.ResetDTO;
import br.com.projeto.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid AutheticationDTO data) {
        return ResponseEntity.ok(authenticationService.login(data));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegisterDTO data) {
        return ResponseEntity.ok(authenticationService.register(data));
    }

    @PostMapping("/google")
    public Map<String, String> loginWithGoogle(@RequestBody Map<String, String> body){
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

    @GetMapping("")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("{\"message\": \"essa e a rota de autenticação\"}");
    }
}
