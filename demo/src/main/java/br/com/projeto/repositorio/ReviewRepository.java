package br.com.projeto.repositorio;



import br.com.projeto.models.review.Review;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.models.watchlist.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUsuario(Usuario usuario, Pageable pageable);


    List<Review> findByUsuario(Usuario usuario);
    Page<Review> findByMediaIdAndMediaType(Long mediaId, MediaType mediaType, Pageable pageable);
    int countByUsuario(Usuario usuario);
}
