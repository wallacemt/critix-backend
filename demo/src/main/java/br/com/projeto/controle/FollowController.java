package br.com.projeto.controle;

import br.com.projeto.dto.UsuarioFollowDTO;
import br.com.projeto.models.notifications.NotificationType;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.service.FollowerService;
import br.com.projeto.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getisFollowing(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        try {
            return ResponseEntity.ok(followerService.getIsFollow(id, usuario));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<Page<UsuarioFollowDTO>> getFollowers(
            @PathVariable Long userId,
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UsuarioFollowDTO> followers = followerService.getFollowers(userId, pageable, usuario);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{id}/followings")
    public ResponseEntity<Page<UsuarioFollowDTO>> getFollowing(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            return ResponseEntity.ok(followerService.getFollowing(id, pageable, usuario));
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
            ResponseEntity<String> response = followerService.follow(usuario, id);
            String message = "🎉 " + usuario.getNome() + " começou a te seguir!";
            notificationService.sendNotification(
                    id,
                    usuario.getImagePath(),
                    usuario.getNome(),
                    usuario.getId(),
                    message,
                    usuario.getId().toString(),
                    NotificationType.follow
            );

            return response;
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
            return followerService.unfollow(usuario, id);
        } catch (UsernameNotFoundException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor");
        }
    }
}
