package br.com.projeto.models.review;

import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.models.watchlist.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "review")// Nome da tabela no banco de dados
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="user_id", nullable = false)
    private Usuario usuario; //Relacionamento com a entidade de usuario

    @Column(name="media_id", nullable = false)
    private Long mediaId; //Id da media da review

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType; // Tipo da media da review

    @Column(name="nota",nullable = false)
    private Integer nota; // Nota da review (0-5)

    @Column(name = "content",nullable = false, columnDefinition = "TEXT")
    private String content; // Conteudo da Review

    @Column(name = "contains_spoiler", nullable = false)
    private Boolean containsSpoler = false; //Inicializado como false

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column
    private  LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(nullable = false)
    private Integer deslikes = 0;

    @Column(nullable = false)
    private Integer comentarios = 0;
}
