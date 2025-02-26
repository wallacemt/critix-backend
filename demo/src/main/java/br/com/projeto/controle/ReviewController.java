package br.com.projeto.controle;


import br.com.projeto.dto.ReviewDTO;
import br.com.projeto.dto.UserLikeDTO;
import br.com.projeto.models.review.LikeType;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.watchlist.MediaType;
import br.com.projeto.service.ReviewService;
import br.com.projeto.models.usuario.Usuario;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> getReviews(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(reviewService.getReviews(usuario, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(reviewService.getById(usuario, id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Review não encontrada!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewDTO>> getByIdUser(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = {"id", "dataCriacao"}, direction = Sort.Direction.DESC) Pageable pageable

    ) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId, usuario, pageable));
    }

    @GetMapping("/media/{mediaType}/{mediaId}")
    public ResponseEntity<Page<ReviewDTO>> getByIDMidia(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long mediaId,
            @PathVariable MediaType mediaType,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable

    ) {
        return ResponseEntity.ok(reviewService.getReviewsByMediaId(mediaId, mediaType, usuario, pageable));
    }

    @PostMapping
    public ResponseEntity<?> postReview(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody ReviewDTO reviewDTO) {
        try {
            return ResponseEntity.ok(reviewService.postReview(reviewDTO, usuario));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Review já adicionada a essa mídia.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor");
        }
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

    @GetMapping("/{id}/{interaction}")
    public ResponseEntity<?> verifyInteration(
            @PathVariable Long id,
            @PathVariable LikeType interaction,
            @AuthenticationPrincipal Usuario usuario) {
        try {
            return ResponseEntity.ok(reviewService.verifyInteration(id, usuario, interaction));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{reviewId}/users")
    public ResponseEntity<List<UserLikeDTO>> getUsersWhoLikedReview(@PathVariable Long reviewId) {
        List<UserLikeDTO> usersWhoLiked = reviewService.getUserWhoLikedReview(reviewId);
        return ResponseEntity.ok(usersWhoLiked);
    }

    @GetMapping("/following")
    public ResponseEntity<Page<ReviewDTO>> getReviewFollowing(
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        try {
            Page<ReviewDTO> reviews = reviewService.getReviewsFollowing(usuario, pageable);
            if (reviews.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(reviews);
            }
            return ResponseEntity.ok(reviews); // Retorna status 200 e as reviews
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
