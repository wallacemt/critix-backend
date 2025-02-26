package br.com.projeto.service;


import br.com.projeto.dto.ReviewDTO;
import br.com.projeto.models.review.LikeType;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.review.ReviewLike;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.ReviewLikeRepository;
import br.com.projeto.repositorio.ReviewRepository;
import br.com.projeto.repositorio.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional  // gerenciamento de transações e evita erros comuns.
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    public List<ReviewDTO> getReviews(Usuario usuario) {
        return reviewRepository.findByUsuario(usuario).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByUserId(Long userId) {
        Usuario usuarioEntity = usuarioRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado!"));

        return reviewRepository.findByUsuario(usuarioEntity).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByMediaId(Long mediaId) {
        return reviewRepository.findByMediaId(mediaId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ReviewDTO postReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    public void deleteReview(Long id, Usuario usuario) {
        List<Review> reviewUser = reviewRepository.findByUsuario(usuario);
        if (reviewUser.isEmpty()) {
            throw new EntityNotFoundException("Nehum Item econtrado!");
        }
        Review reviewToDelete = null;
        for (Review review : reviewUser) {
            if (review.getId().equals(id)) {
                reviewToDelete = review;
                break;
            }
        }
        if (reviewToDelete == null) {
            throw new IllegalArgumentException("Avaliação com ID " + id + " não encontrada.");

        }
        try {
            reviewRepository.delete(reviewToDelete);
        } catch (Exception e) {
            throw new InternalError("Erro ao deletar avaliação: " + e.getMessage());
        }
    }


    public ReviewDTO partialUpdateReview(Long id, Map<String, Object> updates, Usuario usuario) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();

            if (!review.getUsuario().equals(usuario)) {
                throw new EntityNotFoundException("Avaliação com ID " + id + " não encontrada.");
            }

            updates.forEach((campo, valor) -> {
                switch (campo) {
                    case "nota":
                        review.setNota((Integer) valor);
                        break;
                    case "content":
                        review.setContent((String) valor);
                        break;
                    case "containsSpoiler":
                        review.setContainsSpoler((Boolean) valor);
                        break;
                    default:
                        throw new IllegalArgumentException("Campo inválido: " + campo);
                }
            });

            review.setUpdatedAt(LocalDateTime.now());

            Review updatedReview = reviewRepository.save(review);
            return convertToDTO(updatedReview);
        } else {
            throw new UsernameNotFoundException("Review not found");
        }
    }


    public ReviewDTO toggleLike(Long id, Usuario usuario) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Review not found"));


        System.out.println("Buscando like para UserID: " + usuario.getId() + " e ReviewID: " + id);
        Optional<ReviewLike> existingLike = reviewLikeRepository.findByUsuarioAndReview(usuario, review);
        System.out.println("Resultado da busca: " + existingLike);

        if (existingLike.isPresent()) {
            ReviewLike like = existingLike.get();


            if (like.getLikeType().equals(LikeType.like)) {
                reviewLikeRepository.delete(like);
            } else if (like.getLikeType().equals(LikeType.dislike)) {
                like.setLikeType(LikeType.like);
                reviewLikeRepository.save(like);
            }
        } else {
            reviewLikeRepository.save(new ReviewLike(usuario, review, LikeType.like));
        }

        updateLikeDislikeCount(review);
        return convertToDTO(reviewRepository.save(review));
    }


    public ReviewDTO toggleDislike(Long id, Usuario usuario) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Review not found"));
        Optional<ReviewLike> existingLike = reviewLikeRepository.findByUsuarioAndReview(usuario, review);
        if (existingLike.isPresent()) {
            ReviewLike like = existingLike.get();
            if (like.getLikeType() == LikeType.dislike) {
                reviewLikeRepository.delete(like);
            } else if (like.getLikeType() == LikeType.like) {
                like.setLikeType(LikeType.dislike);
                reviewLikeRepository.save(like);
            }
        } else {
            reviewLikeRepository.save(new ReviewLike(usuario, review, LikeType.dislike));
        }

        updateLikeDislikeCount(review);
        return convertToDTO(reviewRepository.save(review));
    }


    private void updateLikeDislikeCount(Review review) {
        long likeCount = reviewLikeRepository.countByReviewAndLikeType(review, LikeType.like);
        long dislikeCount = reviewLikeRepository.countByReviewAndLikeType(review, LikeType.dislike);
        review.setLikes((int) likeCount);
        review.setDeslikes((int) dislikeCount);
    }


    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getUsuario().getId(),
                review.getMediaId(),
                review.getMediaType(),
                review.getNota(),
                review.getContent(),
                review.getContainsSpoler(),
                review.getDataCriacao(),
                review.getUpdatedAt(),
                review.getLikes(),
                review.getDeslikes(),
                review.getComentarios()
        );
    }

    // Retorna uma lista das reviews que o usuário autenticado segue
    public List<ReviewDTO> getReviewsFollowing(Usuario usuario){
        try {
            // Busca as avaliações dos usuários que o usuário autenticado segue com no máximo de 20 avaliações
            List<Review> reviews = reviewRepository.findReviewsByFollowedUsers(usuario.getId(),
                    PageRequest.of(0, 20, Sort.by("dataCriacao").descending()));

            return reviews.stream()
                    .map(ReviewDTO::new)// Converte Review para ReviewDTO
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new RuntimeException("Erro ao buscar avaliações dos usuários seguidos",e);
        }

    }
}
