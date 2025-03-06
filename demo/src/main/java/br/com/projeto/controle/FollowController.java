package br.com.projeto.controle;

import br.com.projeto.dto.UsuarioFollowDTO;
import br.com.projeto.models.notifications.NotificationType;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getisFollowing(@PathVariable String username, @AuthenticationPrincipal Usuario usuario) {
        try {
            return ResponseEntity.ok(followerService.getIsFollow(username, usuario));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<Page<UsuarioFollowDTO>> getFollowers(
            @PathVariable String username,
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<UsuarioFollowDTO> followers = followerService.getFollowers(username, pageable, usuario);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{username}/followings")
    public ResponseEntity<Page<UsuarioFollowDTO>> getFollowing(
            @PathVariable String username,
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        try {
            return ResponseEntity.ok(followerService.getFollowing(username, pageable, usuario));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{username}")
    public ResponseEntity<String> follow(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String username
    ) {
        try {
            ResponseEntity<String> response = followerService.follow(usuario, username);
            String message = "🎉 " + usuario.getNome() + " começou a te seguir!";
            Usuario destination =  usuarioRepository.findByUsernameUser(username).orElseThrow();
            notificationService.sendNotification(
                    destination,
                    usuario.getImagePath(),
                    usuario.getNome(),
                    usuario,
                    message,
                    usuario.getUsernameUser(),
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

    @DeleteMapping("/{username}")
    public ResponseEntity<String> unfollow(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable String username
    ) {
        try {
            return followerService.unfollow(usuario, username);
        } catch (UsernameNotFoundException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor");
        }
    }
}
