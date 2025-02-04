package br.com.projeto.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long userId;
    private Long mediaId;
    private String mediaType;
    private Double nota;
    private String content;
    private Boolean containsSpoiler;
    private LocalDateTime dataCriacao;
    private Integer likes;
    private Integer dislikes;
    private Integer comentarios;
}
