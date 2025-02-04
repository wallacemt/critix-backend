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
import java.util.List;

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
    public ResponseEntity<ReviewDTO> postReview(@AuthenticationPrincipal Usuario usuario, @RequestBody ReviewDTO reviewDTO) {
        if (usuario == null) {
            System.out.println("Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        System.out.println("Usuário autenticado: " + usuario.getId());
        Review review = new Review();
        review.setUserId(usuario.getId());
        review.setMediaId(reviewDTO.getMediaId());
        review.setMediaType(reviewDTO.getMediaType());
        review.setNota(reviewDTO.getNota());
        review.setContent(reviewDTO.getContent());
        review.setContainsSpoiler(reviewDTO.getContainsSpoiler());
        review.setComentarios(reviewDTO.getComentarios());
        return ResponseEntity.ok(reviewService.postReview(review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> editReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.editReview(id, reviewDTO));
    }
}
