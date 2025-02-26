package br.com.projeto.repositorio;

import br.com.projeto.models.comment.Comment;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByReviewId(Long reviewId, Pageable pageable);

    int countByReview(Review review);
}

