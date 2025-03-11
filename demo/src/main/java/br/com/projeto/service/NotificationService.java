package br.com.projeto.service;

import br.com.projeto.dto.NotificationRequestDTO;
import br.com.projeto.dto.ReviewDTO;
import br.com.projeto.models.notifications.Notification;
import br.com.projeto.models.notifications.NotificationType;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.NotificationRepository;
import br.com.projeto.repositorio.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(Usuario destination, String remetenteImage, String remetenteName, Usuario remetente,
                                 String message, String reference, NotificationType type) {
        // Verifica se já existe uma notificação com os mesmos dados, exceto createdAt
        Optional<Notification> notificationOptional = notificationRepository
                .findByDestinationAndRemetenteAndRemetenteImageAndRemetenteNameAndMessageAndReferenceAndType(
                        destination, remetente, remetenteImage, remetenteName, message, reference, type
                );

        if(remetente.getId().equals(destination.getId())){
            return;
        }

        if (notificationOptional.isEmpty()) {
            // Se não existir, cria e salva uma nova notificação
            Notification notification = Notification.builder()
                    .destination(destination)
                    .remetente(remetente)
                    .remetenteImage(remetenteImage)
                    .remetenteName(remetenteName)
                    .message(message)
                    .reference(reference)
                    .type(type)
                    .createdAt(LocalDateTime.now())
                    .seen(false)
                    .build();

            notificationRepository.save(notification);
        } else {
            // Caso a notificação já exista, apenas atualiza o createdAt
            notificationOptional.get().setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notificationOptional.get());
        }
        // Envia a notificação via WebSocket (ou outro mecanismo de envio)
        messagingTemplate.convertAndSend("/topic/notification/" + destination.getId(), message);
    }


    public Page<NotificationRequestDTO> getNotifications(Long id, Pageable pageable) {
        Usuario destination = usuarioRepository.findById(id).orElseThrow();
        Page<Notification> notificationPage = notificationRepository.findByDestinationOrderByCreatedAtDesc(destination, pageable);

        List<NotificationRequestDTO> notificationDTO = notificationPage.getContent().stream()
                .map(notification -> convertToDTO(notification))
                .collect(Collectors.toList());

        return new PageImpl<>(notificationDTO, pageable, notificationPage.getTotalElements());
    }

    public void seenNotification(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setSeen(true);
            notificationRepository.save(notification);
        });
        return;
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
        return;
    }

    @Transactional
    public void deleteAllNotification(Usuario usuario){
        notificationRepository.deleteAllByDestination(usuario);
    }

    private NotificationRequestDTO convertToDTO(Notification notification) {
        return new NotificationRequestDTO(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getDestination().getId(),
                notification.getRemetente().getId(),
                notification.getRemetente().getNome(),
                notification.getRemetente().getImagePath(),
                notification.isSeen(),
                notification.getMessage(),
                notification.getType(),
                notification.getReference()
        );
    }
}

