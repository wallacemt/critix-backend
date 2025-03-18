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
    private String username;
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
    private Boolean isUser = false;

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.userId = review.getUsuario().getId();
        this.username = review.getUsuario().getUsernameUser();
        this.mediaId = review.getMediaId();
        this.mediaType = review.getMediaType();
        this.nota = review.getNota();
        this.content = review.getContent();
        this.containsSpoiler = review.getContainsSpoler();
        this.dataCriacao = review.getDataCriacao();
        this.updatedAt = review.getUpdatedAt();
        this.likes = review.getLikes();
        this.deslikes = review.getDeslikes();
        this.comentarios = review.getComentarios();
    }

}
