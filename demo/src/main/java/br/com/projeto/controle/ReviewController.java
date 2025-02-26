package br.com.projeto.controle;


import br.com.projeto.dto.ReviewDTO;
import br.com.projeto.models.review.Review;
import br.com.projeto.service.ReviewService;
import br.com.projeto.models.usuario.Usuario;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getReviews(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(reviewService.getReviews(usuario));
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
    public ResponseEntity<ReviewDTO> postReview(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody ReviewDTO reviewDTO) {
        Review review = new Review();
        review.setUsuario(usuario);
        review.setMediaId(reviewDTO.getMediaId());
        review.setMediaType(reviewDTO.getMediaType());
        review.setNota(reviewDTO.getNota());
        review.setContent(reviewDTO.getContent());
        review.setContainsSpoler(reviewDTO.getContainsSpoiler());
        review.setDataCriacao(LocalDateTime.now());
        review.setLikes(0);
        review.setDeslikes(0);
        review.setComentarios(0);
        return ResponseEntity.ok(reviewService.postReview(review));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id) {
        try {
            reviewService.deleteReview(id, usuario);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }


    @PatchMapping("/{id}")
    public ResponseEntity<ReviewDTO> partialUpdateReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal Usuario usuario
    ) {
        try {
            ReviewDTO updatedReviewDTO = reviewService.partialUpdateReview(id, updates, usuario);
            return ResponseEntity.ok(updatedReviewDTO);
        } catch (UsernameNotFoundException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }


    @PutMapping("/{id}/like")
    public ResponseEntity<ReviewDTO> addLike(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(reviewService.toggleLike(id, usuario));
        } catch (UsernameNotFoundException e) {
            System.out.println("Erro: Review não encontrada - ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}/dislike")
    public ResponseEntity<ReviewDTO> addDislike(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            return ResponseEntity.ok(reviewService.toggleDislike(id, usuario));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    // Retorna uma lista das reviews que o usuário autenticado segue
    @GetMapping("/following")
    public ResponseEntity<List<ReviewDTO>> getReviewFollowing(@AuthenticationPrincipal Usuario usuario){
        try{
            List<ReviewDTO> reviews = reviewService.getReviewsFollowing(usuario);
            return ResponseEntity.ok(reviews);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}
