package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.repositories.ImageRepository;
import pt.uminho.di.aa.miradourum.repositories.ReviewRepository;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/pi")
public class PontoInteresseController {

    private final PontoInteresseService pontoInteresseService;
    private final JwtService jwtService;
    private final ImageRepository imageRepository;
    private final ReviewRepository reviewRepository;

    public PontoInteresseController(PontoInteresseService pontoInteresseService, JwtService jwtService, ImageRepository imageRepository, ReviewRepository reviewRepository) {
        this.pontoInteresseService = pontoInteresseService;
        this.jwtService = jwtService;
        this.imageRepository = imageRepository;
        this.reviewRepository = reviewRepository;
    }

    //rota que adiciona um novo ponto de interesse
    @PostMapping
    public ResponseEntity<?> createPonto(@RequestBody Map<String, String> pontodata, @RequestHeader("Authorization") String authHeader) {

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

            // Define required fields
            String[] requiredFields = {"latitude", "longitude", "name", "description", "dificulty", "accessibility", "premium"};
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

                PontoInteresse pontoInteresse = new PontoInteresse(latitude, longitude, name, description, dificulty, accessibility, false, premium, 0.0, creationDate);

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


    //rota que adiciona uma review a um ponto de interesse
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

            PontoInteresse point = pontoInteresseService.getById(id);
            if(point == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing point of interest.");
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
            //Comment varchar(255), Rating int(10), CreationDate date, UserID int(10) NOT NULL, PontoInteresseID int(10) NOT NULL, PRIMARY KEY (ID));
            //Review review = new Review(Integer rating, String comment, Date creationDate, Long userid, PontoInteresse pontoInteresse, List< Image > images)
            try {
                String comment = String.valueOf(reviewdata.get("comment"));
                Integer rating = Integer.valueOf(String.valueOf(reviewdata.get("rating")));
                LocalDateTime creationDate = LocalDateTime.now();
                Date creationDateConverted = Date.from(creationDate.atZone(ZoneId.systemDefault()).toInstant());

                Review review = new Review(rating,comment,creationDateConverted,userId,point);



                Object rawImages = reviewdata.get("images");

                if (!(rawImages instanceof List<?>)) {
                    return ResponseEntity.badRequest().body("Invalid format for 'images' — expected an array of strings.");
                }

                List<?> rawList = (List<?>) rawImages;
                List<Image> images = new ArrayList<>();

                for (Object obj : rawList) {
                    if (!(obj instanceof String)) {
                        return ResponseEntity.badRequest().body("Invalid image entry — must be a string.");
                    }
                    Image img = new Image();
                    img.setReview(review);
                    img.setUrl((String) obj);
                    images.add(img);
                    imageRepository.save(img);
                }

                review.setImages(images);
                reviewRepository.save(review);

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



    // Obter ponto de interesse e seus detalhes
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getPontoInteresseDetails(@PathVariable("id") String id, @RequestHeader("Authorization") String authHeader) {
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

            PIDetailsDto ponto = pontoInteresseService.getById(Long.valueOf(id));
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

    //TODO -> ver se criamos um controller para o admin
    // admin valida ponto de interesse Post("/validar?")
    /*@PostMapping
    public ResponseEntity<String> createPonto(@RequestBody PontoInteresse ponto) {
        pontoInteresseService.savePontoInteresse(ponto);
        return ResponseEntity.ok("Ponto de Interesse criado com sucesso");
    }
     */

}
