package br.com.projeto.repositorio;


import br.com.projeto.models.review.Review;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.models.watchlist.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUsuario(Usuario usuario, Pageable pageable);

    List<Review> findByUsuario(Usuario usuario);

    Page<Review> findByMediaIdAndMediaType(Long mediaId, MediaType mediaType, Pageable pageable);

    int countByUsuario(Usuario usuario);

    @Query("SELECT r FROM Review r " +
            "JOIN r.usuario u " +  // Junção com a tabela de usuários (onde r.usuario é o relacionamento)
            "JOIN Follower f ON f.following.id = u.id " + // Junção com a tabela de followers usando a chave estrangeira 'following_id'
            "WHERE f.follower.id = :usuarioId " + // Condição de filtro: apenas os seguidores do usuário autenticado
            "ORDER BY r.dataCriacao DESC") // Ordenação por data de criação da review
    Page<Review> findReviewsByFollowedUsers(@Param("usuarioId") Long usuarioId, Pageable pageable);

}
