package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.models.ImagePontoInteresse;
import pt.uminho.di.aa.miradourum.projections.PontoInteresse.PIDetailsFullProjection;
import pt.uminho.di.aa.miradourum.projections.PontoInteresse.PIDetailsShortProjection;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.services.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final JwtService jwtService;
    private final ImageService imageService;
    private final ReviewService reviewService;

    public ReviewController(JwtService jwtService, ImageService imageService, ReviewService reviewService) {
        this.jwtService = jwtService;
        this.imageService = imageService;
        this.reviewService = reviewService;
    }


    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @RequestBody Map<String, Object> reviewData,
                                          @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            Optional<Review> optionalReview = reviewService.getById(reviewId);
            if (optionalReview.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
            }

            Review review = optionalReview.get();

            if (!review.getUserid().equals(userId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized to edit this review");
            }

            // Validate fields
            String[] requiredFields = {"rating", "comment"};
            List<String> missingFields = new ArrayList<>();
            for (String field : requiredFields) {
                if (!reviewData.containsKey(field) ||
                        reviewData.get(field) == null ||
                        (reviewData.get(field) instanceof String && ((String) reviewData.get(field)).isBlank())) {
                    missingFields.add(field);
                }
            }

            if (!missingFields.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing required fields");
                errorResponse.put("required_fields", requiredFields);
                errorResponse.put("missing_fields", missingFields);
                return ResponseEntity.badRequest().body(errorResponse);
            }

            try {
                // Update review fields
                String comment = String.valueOf(reviewData.get("comment"));
                Integer rating = Integer.valueOf(String.valueOf(reviewData.get("rating")));

                review.setComment(comment);
                review.setRating(rating);
                review.setCreationDate(new Date()); // optionally update timestamp

                reviewService.updateReview(review); // persist changes

                // Handle images
                Object rawImages = reviewData.get("images");
                if(rawImages!=null) {


                    if (!(rawImages instanceof List<?>)) {
                        return ResponseEntity.badRequest().body("Invalid format for 'images' — expected an array of strings.");
                    }

                    List<?> rawList = (List<?>) rawImages;
                    List<Image> newImages = new ArrayList<>();

                    for (Object obj : rawList) {
                        if (!(obj instanceof String)) {
                            return ResponseEntity.badRequest().body("Invalid image entry — must be a string.");
                        }
                        Image image = imageService.saveImage((String) obj, review);
                        newImages.add(image);
                    }
                    reviewService.addImages(review.getId(), newImages);
                }
                // Optionally clear existing images before re-adding (depends on your logic)
                Review updatedReview = reviewService.getById(review.getId()).get();
                int size = updatedReview.getImages().size(); // Force lazy loading
                return ResponseEntity.ok(updatedReview);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid data format: " + e.getMessage());
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId,
                                          @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            Optional<Review> optionalReview = reviewService.getById(reviewId);
            if (optionalReview.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found");
            }

            Review review = optionalReview.get();
            int role = jwtService.extractRole(token);
            //admin pode apagar
            if (!review.getUserid().equals(userId) && role != 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized to delete this review");
            }

            reviewService.deleteReviewById(review); // implement this in your service/repository

            return ResponseEntity.ok("Review deleted successfully");

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired or invalid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the review");
        }
    }

}
