package br.com.projeto.models.followers;

import br.com.projeto.models.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "followers")
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Usuario follower;

    @ManyToOne
    @JoinColumn(name = "following_id")
    private Usuario following;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}