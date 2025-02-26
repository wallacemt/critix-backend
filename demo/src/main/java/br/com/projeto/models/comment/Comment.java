package br.com.projeto.models.comment;

import br.com.projeto.models.review.Review;
import br.com.projeto.models.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;


    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;
}
