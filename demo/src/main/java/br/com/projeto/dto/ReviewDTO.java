package br.com.projeto.dto;

import br.com.projeto.models.review.Review;
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

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.mediaId = review.getMediaId();
        this.userId = review.getUsuario().getId();
        String username = review.getUsuario().getUsername();
        this.content = review.getContent();
        this.nota = review.getNota();
        this.containsSpoiler = review.getContainsSpoler();
        this.likes = review.getLikes();
        this.dataCriacao = review.getDataCriacao();
    }
}
