package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.projections.PontoInteresse.PIDetailsFullProjection;
import pt.uminho.di.aa.miradourum.projections.PontoInteresse.PIDetailsShortProjection;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.services.ImageService;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.ReviewService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/pi")
public class PontoInteresseController {

    private final PontoInteresseService pontoInteresseService;
    private final JwtService jwtService;
    private final ImageService imageService;
    private final ReviewService reviewService;

    public PontoInteresseController(PontoInteresseService pontoInteresseService, JwtService jwtService, ImageService imageService, ReviewService reviewService) {
        this.pontoInteresseService = pontoInteresseService;
        this.jwtService = jwtService;
        this.imageService = imageService;
        this.reviewService = reviewService;
    }

    // Add new PI
    @PostMapping
    public ResponseEntity<?> createPonto(@RequestBody Map<String, String> pontodata, @RequestHeader("Authorization") String authHeader) {

        // 1. Check if token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }
        if (pontodata == null || pontodata.isEmpty()) {
            return ResponseEntity.badRequest().body("Request body is missing or empty");
        }


        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            // 2. Check if token is expired
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            // 3. Extract user ID
            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            // Define required fields
            String[] requiredFields = {"latitude", "longitude", "name", "description", "dificulty", "accessibility", "premium","creatorEmail"};
            List<String> missingFields = new ArrayList<>();

            // Check for missing fields
            for (String field : requiredFields) {
                if (!pontodata.containsKey(field) || pontodata.get(field).isBlank()) {
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
                Double latitude = Double.valueOf(pontodata.get("latitude"));
                Double longitude = Double.valueOf(pontodata.get("longitude"));
                String name = pontodata.get("name");
                String description = pontodata.get("description");
                Integer dificulty = Integer.valueOf(pontodata.get("dificulty"));
                Boolean accessibility = Boolean.valueOf(pontodata.get("accessibility"));
                Boolean premium = Boolean.valueOf(pontodata.get("premium"));
                Double score = (double)0;
                LocalDateTime creationDate = LocalDateTime.now();
                String CreatorEmail = pontodata.get("creatorEmail");

                PontoInteresse pontoInteresse = new PontoInteresse(latitude, longitude, name, description, dificulty, accessibility, false, premium, 0.0, creationDate,CreatorEmail);

                pontoInteresseService.savePontoInteresse(pontoInteresse);
                return ResponseEntity.ok(pontoInteresse);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid data format: " + e.getMessage());
            }

        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }


    // Add new Review to PI
    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> createReviewOnPonto(@PathVariable Long id,@RequestBody Map<String, Object> reviewdata, @RequestHeader("Authorization") String authHeader) {

        // 1. Check if token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            // 2. Check if token is expired
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            // 3. Extract user ID
            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            Optional<PontoInteresse> optionalPonto = pontoInteresseService.getByIdComplete(id);
            if (optionalPonto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ponto de interesse not found");
            }

            PontoInteresse point = optionalPonto.get();

            if(point.getState()==false){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Point is still inactive.");
            }

            // Define required fields
            String[] requiredFields = {"rating", "comment", "images"};
            List<String> missingFields = new ArrayList<>();

            // Check for missing fields
            for (String field : requiredFields) {
                if (!reviewdata.containsKey(field) ||
                        reviewdata.get(field) == null ||
                        (reviewdata.get(field) instanceof String && ((String) reviewdata.get(field)).isBlank())) {
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
                String comment = String.valueOf(reviewdata.get("comment"));
                Integer rating = Integer.valueOf(String.valueOf(reviewdata.get("rating")));
                LocalDateTime creationDate = LocalDateTime.now();
                Date creationDateConverted = Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant());

                Object rawImages = reviewdata.get("images");
                //**
                //**
                //Todo
                //Depois aqui meter a logica de pegar no byte[] das images que vai vir no frontend, mandar para o image service que as enfia no minio
                if (!(rawImages instanceof List<?>)) {
                    return ResponseEntity.badRequest().body("Invalid format for 'images' — expected an array of strings.");
                }

                List<?> rawList = (List<?>) rawImages;
                List<Image> images = new ArrayList<>();

                Review rev = reviewService.saveReview(rating,comment,creationDateConverted,userId,point);

                for (Object obj : rawList) {
                    if (!(obj instanceof String)) {
                        return ResponseEntity.badRequest().body("Invalid image entry — must be a string.");
                    }
                    Image image = imageService.saveImage((String) obj,null,rev);
                    images.add(image);
                }

                reviewService.addImages(rev.getId(),images);


                point.addReview(rev);
                pontoInteresseService.savePontoInteresse(point);
                return ResponseEntity.ok(point);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid data format: " + e.getMessage());
            }

        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }



    // Get PI Details (Short)
    @GetMapping("/shortdetails/{id}")
    public ResponseEntity<?> getPontoInteresseShortDetails(@PathVariable("id") String id, @RequestHeader("Authorization") String authHeader) {
        // 1. Check if token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            // 2. Check if token is expired
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            // 3. Extract user ID
            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            PIDetailsShortProjection ponto = pontoInteresseService.getById(Long.valueOf(id), PIDetailsShortProjection.class);
            if (ponto == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PI not found");

            return ResponseEntity.ok(ponto);

        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    // Get PI Details (Full)
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getPontoInteresseFullDetails(@PathVariable("id") String id, @RequestHeader("Authorization") String authHeader) {
        // 1. Check if token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            // 2. Check if token is expired
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            // 3. Extract user ID
            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            PIDetailsFullProjection ponto = pontoInteresseService.getById(Long.valueOf(id), PIDetailsFullProjection.class);
            if (ponto == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PI not found");

            return ResponseEntity.ok(ponto);

        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    // Página inicial -> filtros
    @GetMapping
    public ResponseEntity<List<PontoInteresse>> getFiltered(
            @RequestParam String userCoordinates,
            @RequestParam(required = false, defaultValue = "10") int distancia,
            @RequestParam(required = false) Double score,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Integer visitantes,
            @RequestParam(required = false) Boolean acessibilidade,
            @RequestParam(required = false) Integer dificuldade
    ) {
        LocalDateTime dataFinal = (date != null) ? LocalDateTime.parse(date) : null;

        List<PontoInteresse> resultado = pontoInteresseService.getNewest(
                userCoordinates,
                distancia,
                (score != null) ? score : 0.0,
                dataFinal,
                (visitantes != null) ? visitantes : 0,
                (acessibilidade != null) ? acessibilidade : false,
                (dificuldade != null) ? dificuldade : 0
        );
        return ResponseEntity.ok(resultado);
    }

}
