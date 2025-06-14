package pt.uminho.di.aa.miradourum.dto;

import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.projections.Review.ReviewProjection;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewDTO {
    private Long id;
    private String comment;
    private Integer rating;
    private LocalDateTime creationDate;
    private Long userId;
    private String username; // username em vez de userId
    private List<Image> images;

    public ReviewDTO(ReviewProjection review, String username) {
        this.id = review.getId();
        this.comment = review.getComment();
        this.rating = review.getRating();
        this.creationDate = review.getCreationDate();
        this.username = username;
        this.images = review.getImages();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Long getUserId() {   
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
