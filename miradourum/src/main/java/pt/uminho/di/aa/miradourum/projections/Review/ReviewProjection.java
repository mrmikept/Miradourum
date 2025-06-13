package pt.uminho.di.aa.miradourum.projections.Review;

import pt.uminho.di.aa.miradourum.models.Image;

import java.util.Date;
import java.util.List;

public interface ReviewProjection {
    Long getId();
    String getComment();
    Integer getRating();
    Date getCreationDate();
    Long getUserid();
    List<Image> getImages();
}
