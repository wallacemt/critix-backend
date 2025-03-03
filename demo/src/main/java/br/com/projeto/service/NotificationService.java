package br.com.projeto.service;

import br.com.projeto.models.notifications.Notification;
import br.com.projeto.models.notifications.NotificationType;
import br.com.projeto.repositorio.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(Long userId, String remetenteImage, String remetenteName, Long remetenteId,
                                 String message, String reference, NotificationType type) {
        // Verifica se já existe uma notificação com os mesmos dados, exceto createdAt
        Optional<Notification> notificationOptional = notificationRepository
                .findByUserIdAndRemetenteIdAndRemetenteImageAndRemetenteNameAndMessageAndReferenceAndType(
                        userId, remetenteId, remetenteImage, remetenteName, message, reference, type
                );

        if (notificationOptional.isEmpty()) {
            // Se não existir, cria e salva uma nova notificação
            Notification notification = Notification.builder()
                    .userId(userId)
                    .remetenteId(remetenteId)
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
        messagingTemplate.convertAndSend("/topic/notification/" + userId, message);
    }


    public Page<Notification> getNotifications(Long id, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(id, pageable);
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
}

