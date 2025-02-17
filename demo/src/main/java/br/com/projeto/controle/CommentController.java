package br.com.projeto.controle;


import br.com.projeto.dto.CommentDTO;
import br.com.projeto.models.comment.Comment;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getComments() {
        return ResponseEntity.ok(commentService.getComments());
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<List<CommentDTO>> getByIDMidia(@PathVariable Long reviewId) {
        return ResponseEntity.ok(commentService.getCommentsByReviewId(reviewId));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> postComment(@AuthenticationPrincipal Usuario usuario, @RequestBody CommentDTO commentDTO) {
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (commentDTO.getContent() == null || commentDTO.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Comment comment = new Comment();
        comment.setUserId(usuario.getId());
        comment.setReviewId(commentDTO.getReviewId());
        comment.setContent(commentDTO.getContent());
        return ResponseEntity.ok(commentService.postComment(comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
