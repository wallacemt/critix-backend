package br.com.projeto.controle;



import br.com.projeto.dto.ReviewDTO;
import br.com.projeto.models.review.Review;
import br.com.projeto.service.ReviewService;
import br.com.projeto.models.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getReviews() {
        return ResponseEntity.ok(reviewService.getReviews());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getByIdUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    @GetMapping("/media/{mediaId}")
    public ResponseEntity<List<ReviewDTO>> getByIDMidia(@PathVariable Long mediaId) {
        return ResponseEntity.ok(reviewService.getReviewsByMediaId(mediaId));
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> postReview(@RequestBody ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setUserId(reviewDTO.getUserId());
        review.setMediaId(reviewDTO.getMediaId());
        review.setMediaType(reviewDTO.getMediaType());
        review.setNota(reviewDTO.getNota());
        review.setContent(reviewDTO.getContent());
        review.setContainsSpoiler(reviewDTO.getContainsSpoiler());
        review.setDataCriacao(LocalDateTime.now());
        review.setLikes(0);
        review.setDeslikes(0);
        review.setComentarios(0);
        return ResponseEntity.ok(reviewService.postReview(review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewDTO> partialUpdateReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(reviewService.partialUpdateReview(id, updates));
    }
    @PutMapping("/{id}/like")
    public ResponseEntity<ReviewDTO> addLike(@PathVariable Long id) {
        ReviewDTO updatedReview = reviewService.addLike(id);
        return ResponseEntity.ok(updatedReview);
    }

    @PutMapping("/{id}/deslike")
    public ResponseEntity<ReviewDTO> addDislike(@PathVariable Long id) {
        ReviewDTO updatedReview = reviewService.addDeslike(id);
        return ResponseEntity.ok(updatedReview);
    }
}
