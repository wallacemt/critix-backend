package br.com.projeto.repositorio;



import br.com.projeto.dto.WatchListResponseDTO;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.models.watchlist.MediaStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUsuario(Usuario usuario);
    List<Review> findByMediaId(Long mediaId);

    // Busca as reviews dos usuários que o usuário autenticado segue
    @Query("SELECT r FROM Review r "+
            "JOIN r.usuario u "+ //Busca as reviews e seus respectivos usuários
            "JOIN u.seguindo f "+ // Acessa os usuários que o usuário segue
            "WHERE f.follower.id = :usuarioId "+ // Compara com o ID do usuário autenticado
            "ORDER BY r.dataCriacao DESC ")// Adiciona a ordenação por data de criação
    List<Review> findReviewsByFollowedUsers(@Param("usuarioId") Long usuarioId, Pageable pageable);
}
