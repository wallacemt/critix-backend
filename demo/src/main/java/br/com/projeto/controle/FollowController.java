package br.com.projeto.controle;

import br.com.projeto.dto.UsuarioFollowDTO;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.service.FollowerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {
    @Autowired
    private FollowerService followerService;


    @GetMapping("/{id}")
    public ResponseEntity<Page<UsuarioFollowDTO>> getFollowing(@PathVariable Long id, Pageable pageable) {
        try {
            return ResponseEntity.ok(followerService.getFollowing(id, pageable));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> follow(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id
    ) {
        try {
            followerService.follow(usuario, id);
            return ResponseEntity.ok("Seguindo o usuário com ID " + id);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> unfollow(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id
    ) {
        try {
            followerService.unfollow(usuario, id);
            return ResponseEntity.ok("Deixou de seguir o usuário com ID " + id);
        } catch (UsernameNotFoundException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor");
        }
    }
}
