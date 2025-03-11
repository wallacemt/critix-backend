package br.com.projeto.repositorio;

import br.com.projeto.models.notifications.Notification;
import br.com.projeto.models.notifications.NotificationType;
import br.com.projeto.models.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByDestinationOrderByCreatedAtDesc(Usuario destination, Pageable pageable);

    void deleteAllByDestination(Usuario usuario);

    // Busca uma notificação com os mesmos dados, exceto o createdAt
    Optional<Notification> findByDestinationAndRemetenteAndRemetenteImageAndRemetenteNameAndMessageAndReferenceAndType(
            Usuario destination,
            Usuario remetente,
            String remetenteImage,
            String remetenteName,
            String message,
            String reference,
            NotificationType type);
}
