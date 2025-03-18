package br.com.projeto.models.watchlist;

import br.com.projeto.models.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WatchList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario user;

    private Long mediaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaStatus mediaStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    @Column
    private  LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected  void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
