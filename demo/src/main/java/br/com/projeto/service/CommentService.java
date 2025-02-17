package br.com.projeto.service;


import br.com.projeto.dto.CommentDTO;
import br.com.projeto.models.comment.Comment;
import br.com.projeto.repositorio.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<CommentDTO> getComments() {
        return commentRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentsByReviewId(Long reviewId) {
        return commentRepository.findByReviewId(reviewId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }



    public CommentDTO postComment(Comment comment) {
        Comment savedReview = commentRepository.save(comment);
        return convertToDTO(savedReview);
    }


    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }


    private CommentDTO convertToDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getUserId(),
                comment.getReviewId(),
                comment.getContent(),
                comment.getDataCriacao()
        );
    }
}
