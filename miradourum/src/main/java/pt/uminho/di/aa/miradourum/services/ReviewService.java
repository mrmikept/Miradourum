package pt.uminho.di.aa.miradourum.services;

import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;

import java.util.Date;
import java.util.List;

public interface ReviewService {
    Review saveReview(Integer rating, String comment, Date creationDateConverted, Long userId, PontoInteresse point);
    void addImages(Long revId, List<Image> images);
}
