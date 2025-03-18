package br.com.projeto.controle;

import br.com.projeto.dto.NotificationRequestDTO;
import br.com.projeto.models.notifications.Notification;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user/notifications")
public class NotificationRestController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationRequestDTO>> getNotifications(
            @AuthenticationPrincipal Usuario usuario,
            Pageable pageable
    ){
        return ResponseEntity.ok(notificationService.getNotifications(usuario.getId(), pageable));
    }

    @PutMapping("/{id}/seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable Long id){
        notificationService.seenNotification(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id){
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllNotification(@AuthenticationPrincipal Usuario usuario){
        notificationService.deleteAllNotification(usuario);
        return ResponseEntity.ok().build();
    }


}
