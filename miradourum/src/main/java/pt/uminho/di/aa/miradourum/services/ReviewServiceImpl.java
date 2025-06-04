package pt.uminho.di.aa.miradourum.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.repositories.ReviewRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ImageService imageService;

    @Override
    public Review saveReview(Integer rating, String comment, Date creationDateConverted, Long userId, PontoInteresse point){
        Review review = new Review(rating,comment,creationDateConverted,userId,point);
        reviewRepository.save(review);
        return review;
    }

    @Override
    public void addImages(Long revId, List<Image> images){
        Review rev = reviewRepository.getReferenceById(revId);
        rev.addImages(images);
        reviewRepository.save(rev);
    }

    @Override
    public Optional<Review> getById(Long reviewID){
        return reviewRepository.findById(reviewID);

    }
    @Override
    public void updateReview(Review review){
        reviewRepository.save(review);
    }
    @Transactional
    public void deleteReviewById(Review review) {
        // Step 1: Delete associated images
        List<Image> images = review.getImages();
        if (images != null && !images.isEmpty()) {
            for (Image image : images) {
                imageService.deleteImageById(image.getId()); // ensure this method exists in ImageService
            }
        }

        // Step 2: Delete the review itself
        reviewRepository.delete(review);
    }
    @Override
    public List<Review> getAllReviewsUser(Long userId) {
        return reviewRepository.findAllByUserid(userId);
    }




}
