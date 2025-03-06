package br.com.projeto.service;


import br.com.projeto.dto.ReviewDTO;
import br.com.projeto.dto.UserLikeDTO;
import br.com.projeto.models.notifications.NotificationType;
import br.com.projeto.models.review.LikeType;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.review.ReviewLike;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.models.watchlist.MediaType;
import br.com.projeto.repositorio.CommentRepository;
import br.com.projeto.repositorio.ReviewLikeRepository;
import br.com.projeto.repositorio.ReviewRepository;
import br.com.projeto.repositorio.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    public Page<ReviewDTO> getReviews(Usuario usuario, Pageable pageable) {
        Page<Review> reviewsPage = reviewRepository.findByUsuario(usuario, pageable);

        for (Review review : reviewsPage.getContent()) {
            int comentarioCount = commentRepository.countByReview(review);
            review.setComentarios(comentarioCount);
            updateLikeDislikeCount(review);
            reviewRepository.save(review);
        }
        // Converter cada review em ReviewDTO
        List<ReviewDTO> reviewDTOs = reviewsPage.getContent().stream()
                .map(review -> convertToDTO(review, usuario))
                .collect(Collectors.toList());

        usuario.setReviews(reviewRepository.countByUsuario(usuario));
        usuarioRepository.save(usuario);
        // Retornar a página com os DTOs
        return new PageImpl<>(reviewDTOs, pageable, reviewsPage.getTotalElements());
    }

    public ReviewDTO getById(Usuario usuario, Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Reviews não encontrada."));
        review.setComentarios(commentRepository.countByReview(review));
        updateLikeDislikeCount(review);
        reviewRepository.save(review);
        return convertToDTO(review, usuario);
    }

    public Page<ReviewDTO> getReviewsByUserId(String username, Usuario usuario, Pageable pageable) {

        Usuario usuarioEntity = usuarioRepository.findByUsernameUser(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado!"));


        // Passa o pageable para o repositório
        Page<Review> reviewsPage = reviewRepository.findByUsuario(usuarioEntity, pageable);

        for (Review review : reviewsPage.getContent()) {
            int comentarioCount = commentRepository.countByReview(review);
            review.setComentarios(comentarioCount);
            updateLikeDislikeCount(review);
            reviewRepository.save(review);
        }
        // Converte cada review em ReviewDTO
        List<ReviewDTO> reviewDTOs = reviewsPage.getContent().stream()
                .map(review -> convertToDTO(review, usuario))
                .collect(Collectors.toList());

        // Retorna a página com ReviewDTO
        return new PageImpl<>(reviewDTOs, pageable, reviewsPage.getTotalElements());
    }

    public Page<ReviewDTO> getReviewsByMediaId(Long mediaId, MediaType mediaType, Usuario usuario, Pageable pageable) {
        // Passa o pageable para o repositório
        Page<Review> reviewsPage = reviewRepository.findByMediaIdAndMediaType(mediaId, mediaType, pageable);
        for (Review review : reviewsPage.getContent()) {
            int comentarioCount = commentRepository.countByReview(review);
            review.setComentarios(comentarioCount);
            updateLikeDislikeCount(review);
            reviewRepository.save(review);
        }

        // Converte cada review em ReviewDTO
        List<ReviewDTO> reviewDTOs = reviewsPage.getContent().stream()
                .map(review -> convertToDTO(review, usuario))
                .collect(Collectors.toList());

        // Retorna a página com ReviewDTO
        return new PageImpl<>(reviewDTOs, pageable, reviewsPage.getTotalElements());
    }

    // Retorna uma lista das reviews que o usuário autenticado segue
    public Page<ReviewDTO> getReviewsFollowing(Usuario usuario, Pageable pageable) {
        try {
            // Busca as avaliações dos usuários que o usuário autenticado segue com no máximo de 20 avaliações
            Page<Review> reviews = reviewRepository.findReviewsByFollowedUsers(usuario.getId(), pageable);

            for (Review review : reviews.getContent()) {
                updateLikeDislikeCount(review);
                reviewRepository.save(review);
            }

            List<ReviewDTO> reviewDTOS = reviews.stream()
                    .map(ReviewDTO::new)// Converte Review para ReviewDTO
                    .collect(Collectors.toList());

            return new PageImpl<>(reviewDTOS, pageable, reviews.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar avaliações dos usuários seguidos", e);
        }
    }


    public Page<UserLikeDTO> getUserWhoLikedReview(Long reviewId, LikeType type, Pageable pageable, Usuario usuario) {
        Optional<Review> reviewOptional = reviewRepository.findById(reviewId);
        if (!reviewOptional.isPresent()) {
            throw new EntityNotFoundException("Avaliação não encontrada!");
        }
        Review review = reviewOptional.get();
        Page<ReviewLike> reviewLikes = reviewLikeRepository.findByReviewAndLikeType(review, type, pageable);

        List<UserLikeDTO> usersWhoLiked = reviewLikes.stream()
                .map(reviewLike -> new UserLikeDTO(
                        reviewLike.getUsuario().getId(),
                        reviewLike.getUsuario().getNome(),
                        reviewLike.getUsuario().getUsernameUser(),
                        reviewLike.getUsuario().getImagePath(),
                        reviewLike.getUsuario().equals(usuario)
                ))
                .collect(Collectors.toList());

        return new PageImpl<>(usersWhoLiked, pageable, reviewLikes.getTotalElements());
    }


    public ReviewDTO postReview(ReviewDTO reviewDTO, Usuario usuario) {
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
        List<Review> reviewUser = reviewRepository.findByUsuario(usuario);
        for (Review reviewer : reviewUser) {
            if (reviewer.getMediaId().equals(review.getMediaId()) && reviewer.getMediaType().equals(review.getMediaType())) {
                throw new DuplicateKeyException("Review Já adicionada a essa midia.");
            }
        }
        usuario.setReviews(reviewRepository.countByUsuario(usuario));
        usuarioRepository.save(usuario);
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview, usuario);
    }

    public void deleteReview(Long id, Usuario usuario) {
        List<Review> reviewUser = reviewRepository.findByUsuario(usuario);
        if (reviewUser.isEmpty()) {
            throw new EntityNotFoundException("Nenhum Item encontrado!");
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
            List<ReviewLike> reviewLikes = reviewLikeRepository.findByReview(reviewToDelete);
            if (!reviewLikes.isEmpty()) {
                reviewLikeRepository.deleteAll(reviewLikes);
            }
            reviewRepository.delete(reviewToDelete);
            usuario.setReviews(reviewRepository.countByUsuario(usuario));
            usuarioRepository.save(usuario);
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
            return convertToDTO(updatedReview, usuario);
        } else {
            throw new UsernameNotFoundException("Review not found");
        }
    }


    public boolean verifyInteration(Long id, Usuario usuario, LikeType reviewLike) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Review not found"));


        Optional<ReviewLike> existingLike = reviewLikeRepository.findByUsuarioAndReview(usuario, review);
        if (existingLike.isPresent()) {
            ReviewLike like = existingLike.get();
            if (like.getLikeType().equals(reviewLike)) {
                return true;
            }
        }
        return false;
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
            // Envia uma notificação para o autor da review
            String message = "👍 " + usuario.getNome() + " curtiu sua review";
            Usuario destination = usuarioRepository.findById(review.getUsuario().getId()).orElseThrow();

            notificationService.sendNotification(
                    destination,
                    usuario.getImagePath(),
                    usuario.getNome(),
                    usuario,
                    message,
                    id.toString(),
                    NotificationType.like
            );
            reviewLikeRepository.save(new ReviewLike(usuario, review, LikeType.like));
        }

        updateLikeDislikeCount(review);
        return convertToDTO(reviewRepository.save(review), usuario);
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
        return convertToDTO(reviewRepository.save(review), usuario);
    }

    private void updateLikeDislikeCount(Review review) {
        long likeCount = reviewLikeRepository.countByReviewAndLikeType(review, LikeType.like);
        long dislikeCount = reviewLikeRepository.countByReviewAndLikeType(review, LikeType.dislike);
        review.setLikes((int) likeCount);
        review.setDeslikes((int) dislikeCount);
    }

    public Map<String, Object> calcularNotaGeral(Long mediaId, MediaType mediaType) {
        List<Review> reviews = reviewRepository.findByMediaIdAndMediaType(mediaId, mediaType);

        int notaGeral = -1;
        int totalReviews = reviews.size();

        if (!reviews.isEmpty()) {
            double sum = reviews.stream()
                    .mapToDouble(Review::getNota)
                    .sum();
            double media = sum / totalReviews;
            notaGeral = (int) Math.round(media);
        }

        return Map.of(
                "notaGeral", notaGeral,
                "totalReviews", totalReviews
        );
    }

    public Page<ReviewDTO> getTopReviews(Pageable pageable, Usuario usuario) {
        Pageable sortedByLikes = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "likes")
        );
        Page<Review> reviewsPage = reviewRepository.findAll(sortedByLikes);
        List<ReviewDTO> reviewDTOs = reviewsPage.getContent().stream()
                .map(review -> convertToDTO(review, usuario))
                .collect(Collectors.toList());
        return new PageImpl<>(reviewDTOs, sortedByLikes, reviewsPage.getTotalElements());
    }


    private ReviewDTO convertToDTO(Review review, Usuario usuario) {
        return new ReviewDTO(
                review.getId(),
                review.getUsuario().getId(),
                review.getUsuario().getUsernameUser(),
                review.getMediaId(),
                review.getMediaType(),
                review.getNota(),
                review.getContent(),
                review.getContainsSpoler(),
                review.getDataCriacao(),
                review.getUpdatedAt(),
                review.getLikes(),
                review.getDeslikes(),
                review.getComentarios(),
                review.getUsuario().equals(usuario)
        );
    }
}
