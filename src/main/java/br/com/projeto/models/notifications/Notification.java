package br.com.projeto.models.notifications;

import br.com.projeto.models.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Usuario destination;

    @ManyToOne
    @JoinColumn(name = "remetente_id")
    private Usuario remetente;

    @Column(nullable = false)
    private String remetenteImage;

    @Column(nullable = false)
    private String remetenteName;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean seen = false;

    @Column(nullable = false)
    String reference;
}

