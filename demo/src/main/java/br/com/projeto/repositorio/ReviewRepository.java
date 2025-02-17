package br.com.projeto.repositorio;



import br.com.projeto.models.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
    List<Review> findByMediaId(Long mediaId);
}
