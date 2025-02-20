package br.com.projeto.dto;

import br.com.projeto.models.watchlist.MediaType;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewDTO {
    private Long id;
    private Long userId;
    private Long mediaId;
    private MediaType mediaType;
    private Integer nota;
    private String content;
    private Boolean containsSpoiler;
    private LocalDateTime dataCriacao;
    private LocalDateTime updatedAt;
    private Integer likes;
    private Integer deslikes;
    private Integer comentarios;
}
