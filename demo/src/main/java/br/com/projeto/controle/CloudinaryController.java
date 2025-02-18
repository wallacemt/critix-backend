package br.com.projeto.controle;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import br.com.projeto.service.CloudinaryService;

import java.util.Map;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "bearer-key") //indica que todos os endpoints dentro desse controller exigem autenticação através de um token JWT (JSON Web Token) para serem acessados.
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/api/images")
    public Mono<Map> getImages(@RequestParam(value = "next_cursor", required = false) String nextCursor, @RequestParam("folder_name") String folderName) {
        return cloudinaryService.fetchImages(nextCursor, folderName); // Passa o next_cursor para o serviço
    }

    // Teste de Autenticação Para ver o funcionamento do Token JWT
    @GetMapping("teste")
    public ResponseEntity testeAutenticacao(){
        return ResponseEntity.ok("Usuário Autenticado!");
    }
}