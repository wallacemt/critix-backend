package br.com.projeto.dto;

import br.com.projeto.models.notifications.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {
    private Long id;
    private LocalDateTime created_at;
    private Long destinationId;
    private Long remetente_Id;
    private String remetente_name;
    private String remetente_image;
    private boolean seen;
    private String message;
    private NotificationType type;
    private String reference;
}
