package br.com.projeto.controle;


import br.com.projeto.dto.CommentDTO;
import br.com.projeto.models.comment.Comment;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<Page<CommentDTO>> getByReviewId(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long reviewId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByReviewId(reviewId, pageable, usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByCommentId(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(commentService.getByComment(id, usuario));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }

    @PostMapping("/review/{reviewId}")
    public ResponseEntity<CommentDTO> postComment(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long reviewId,
            @RequestBody CommentDTO commentDTO) {
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (commentDTO.getContent() == null || commentDTO.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(commentService.postComment(commentDTO, reviewId, usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody CommentDTO commentDTO
    ) {
        try {
            return ResponseEntity.ok(commentService.updateComment(id, usuario, commentDTO));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        try {
            commentService.deleteComment(id, usuario);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }
}
