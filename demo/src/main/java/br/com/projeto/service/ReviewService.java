package br.com.projeto.service;


import br.com.projeto.dto.ReviewDTO;
import br.com.projeto.models.review.Review;
import br.com.projeto.repositorio.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public List<ReviewDTO> getReviews() {
        return reviewRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByMediaId(Long mediaId) {
        return reviewRepository.findByMediaId(mediaId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ReviewDTO postReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public ReviewDTO editReview(Long id, ReviewDTO reviewDTO) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setNota(reviewDTO.getNota());
            review.setContent(reviewDTO.getContent());
            review.setContainsSpoiler(reviewDTO.getContainsSpoiler());
            reviewRepository.save(review);
            return convertToDTO(review);
        } else {
            throw new UsernameNotFoundException("Review not found");
        }
    }

    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getUserId(),
                review.getMediaId(),
                review.getMediaType(),
                review.getNota(),
                review.getContent(),
                review.getContainsSpoiler(),
                review.getDataCriacao(),
                review.getLikes(),
                review.getDislikes(),
                review.getComentarios()
        );
    }
}
