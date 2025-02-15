package br.com.projeto.models.review;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long mediaId;
    private String mediaType;
    private Double nota;
    private String content;
    private Boolean containsSpoiler;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private Integer likes = 0;
    private Integer deslikes = 0;
    private Integer comentarios = 0;


}