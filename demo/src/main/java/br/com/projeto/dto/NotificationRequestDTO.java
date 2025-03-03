package br.com.projeto.dto;

import br.com.projeto.models.notifications.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDTO {
    private Long userId;
    private String message;
    private NotificationType type;
    private String reference;
}
