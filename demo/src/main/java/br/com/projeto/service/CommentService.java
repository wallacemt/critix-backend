package br.com.projeto.service;


import br.com.projeto.dto.CommentDTO;
import br.com.projeto.dto.ReviewDTO;
import br.com.projeto.models.comment.Comment;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.CommentRepository;
import br.com.projeto.repositorio.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public Page<CommentDTO> getCommentsByReviewId(Long reviewId, Pageable pageable, Usuario usuario) {
        Page<Comment> commentsPage = commentRepository.findByReviewId(reviewId, pageable);
        List<CommentDTO> commentDTOS = commentsPage.getContent().stream()
                .map(comment -> convertToDTO(comment, usuario))
                .collect(Collectors.toList());

        return new PageImpl<>(commentDTOS, pageable, commentsPage.getTotalElements());
    }


    public CommentDTO postComment(CommentDTO commentDTO, Long reviewId, Usuario usuario) {
        Comment comment = new Comment();
        comment.setUsuario(usuario);
        comment.setReview(reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("Review não encontrada!")));
        comment.setContent(commentDTO.getContent());
        Comment savedReview = commentRepository.save(comment);
        return convertToDTO(savedReview, usuario);
    }

    public CommentDTO getByComment(Long id, Usuario usuario){
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            return convertToDTO(comment.get(), usuario);
        } else {
            throw new EntityNotFoundException("Commentary not found!");
        }
    }

    public CommentDTO updateComment(Long id, Usuario usuario, CommentDTO commentDTO){
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            if (comment.get().getUsuario().equals(usuario)) {
                comment.get().setContent(commentDTO.getContent());
                comment.get().setUpdatedAt(LocalDateTime.now());
                Comment savedComment = commentRepository.save(comment.get());
                return convertToDTO(savedComment, usuario);
            } else{
                throw new IllegalArgumentException("Comentario com ID " + id + " não associada ao usuário!");
            }
        } else {
            throw new EntityNotFoundException("Commentary not found!");
        }
    }

    public void deleteComment(Long id, Usuario usuario) {

        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isPresent()) {
            if (comment.get().getUsuario().equals(usuario)) {
                commentRepository.deleteById(id);
            } else{
                throw new IllegalArgumentException("Comentario com ID " + id + " não associada ao usuário!");
            }
        } else {
            throw new EntityNotFoundException("Commentary not found!");
        }
    }


        private CommentDTO convertToDTO (Comment comment, Usuario usuario){
            return new CommentDTO(
                    comment.getId(),
                    comment.getUsuario().getId(),
                    comment.getReview().getId(),
                    comment.getContent(),
                    comment.getDataCriacao(),
                    comment.getUpdatedAt(),
                    comment.getUsuario().equals(usuario)
            );
        }
    }
