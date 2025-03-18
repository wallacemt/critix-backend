package br.com.projeto.repositorio;

import br.com.projeto.models.review.LikeType;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.review.ReviewLike;
import br.com.projeto.models.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByUsuarioAndReview(Usuario usuario, Review review);

    List<ReviewLike> findByReview(Review review);

    Page<ReviewLike> findByReviewAndLikeType(Review review, LikeType type, Pageable pageable);

    long countByReviewAndLikeType(Review review, LikeType likeType);

}
