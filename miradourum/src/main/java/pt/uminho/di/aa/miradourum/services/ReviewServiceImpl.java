package pt.uminho.di.aa.miradourum.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.repositories.ReviewRepository;

import java.util.Date;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public Review saveReview(Integer rating, String comment, Date creationDateConverted, Long userId, PontoInteresse point){
        Review review = new Review(rating,comment,creationDateConverted,userId,point);
        reviewRepository.save(review);
        return review;
    }

    @Override
    public void addImages(Long revId, List<Image> images){
        Review rev = reviewRepository.getReferenceById(revId);
        rev.setImages(images);
        reviewRepository.save(rev);
    }
}
