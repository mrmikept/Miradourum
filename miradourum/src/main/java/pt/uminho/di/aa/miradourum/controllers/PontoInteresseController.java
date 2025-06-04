package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.models.*;
import pt.uminho.di.aa.miradourum.projections.PontoInteresse.PIDetailsFullProjection;
import pt.uminho.di.aa.miradourum.projections.PontoInteresse.PIDetailsShortProjection;
import pt.uminho.di.aa.miradourum.projections.User.UserProfileProjection;
import pt.uminho.di.aa.miradourum.services.*;

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
    private final ImagePontoInteresseService imagePontoInteresseService;
    private final PontoInteresseServiceImpl pontoInteresseServiceImpl;
    private final UserService userService;
    public PontoInteresseController(PontoInteresseService pontoInteresseService,UserService userService, JwtService jwtService, ImageService imageService, ReviewService reviewService, ImagePontoInteresseService imagePontoInteresseService, PontoInteresseServiceImpl pontoInteresseServiceImpl) {
        this.pontoInteresseService = pontoInteresseService;
        this.jwtService = jwtService;
        this.imageService = imageService;
        this.reviewService = reviewService;
        this.imagePontoInteresseService=imagePontoInteresseService;
        this.pontoInteresseServiceImpl = pontoInteresseServiceImpl;
        this.userService = userService;
    }



    @GetMapping
    public ResponseEntity<?> getAllActivePontosShort(@RequestHeader("Authorization") String authHeader) {
        // 1. Check if token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7);

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

            List<PIDetailsShortProjection> pontos = pontoInteresseService.getAllActive(PIDetailsShortProjection.class);
            return ResponseEntity.ok(pontos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }








    // Add new PI
    @PostMapping
    public ResponseEntity<?> createPonto(@RequestBody Map<String, Object> pontodata, @RequestHeader("Authorization") String authHeader) {

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
                if (!pontodata.containsKey(field) || pontodata.get(field)==null || pontodata.get(field).toString().isBlank()) {
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
                Double latitude = Double.valueOf(pontodata.get("latitude").toString());
                Double longitude = Double.valueOf(pontodata.get("longitude").toString());
                String name = pontodata.get("name").toString();
                String description = pontodata.get("description").toString();
                Integer dificulty = Integer.valueOf(pontodata.get("dificulty").toString());
                Boolean accessibility = Boolean.valueOf(pontodata.get("accessibility").toString());
                Boolean premium = Boolean.valueOf(pontodata.get("premium").toString());
                String creatorEmail = pontodata.get("creatorEmail").toString();
                LocalDateTime creationDate = LocalDateTime.now();

                PontoInteresse pontoInteresse = new PontoInteresse(latitude, longitude, name, description, dificulty, accessibility, false, premium, 0.0, creationDate, creatorEmail);

                pontoInteresseService.savePontoInteresse(pontoInteresse);

                Object rawImages = pontodata.get("imageUrls");
                //**

                if (!(rawImages instanceof List<?>)) {
                    return ResponseEntity.badRequest().body("Invalid format for 'images' — expected an array of strings.");
                }

                List<?> rawList = (List<?>) rawImages;
                List<ImagePontoInteresse> images = new ArrayList<>();

                for (Object obj : rawList) {
                    if (!(obj instanceof String)) {
                        return ResponseEntity.badRequest().body("Invalid image entry — must be a string.");
                    }
                    ImagePontoInteresse image = imagePontoInteresseService.saveImage((String) obj,pontoInteresse);
                    images.add(image);
                }

                pontoInteresseService.addImages(pontoInteresse.getId(),images);

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
                    Image image = imageService.saveImage((String) obj,rev);
                    images.add(image);
                }

                reviewService.addImages(rev.getId(),images);


                point.addReview(rev);
                pontoInteresseService.savePontoInteresse(point);
                User user = userService.getUserById(userId); // Or however you fetch a user

                if (!user.getPontoInteresse().contains(point)) {
                    user.getPontoInteresse().add(point);
                    userService.saveUser(user);
                }
                return ResponseEntity.ok(point);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid data format: " + e.getMessage());
            }

        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    // Add new Review to PI
    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getReviewsOnPonto(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {

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

            List<Review> reviews = point.getReviews();

            return ResponseEntity.ok(reviews);
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
            //ponto ainda esta inativo
            if(!ponto.getState()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ponto is inactive.");
            }
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
            if(!ponto.getState()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ponto is inactive.");
            }
            return ResponseEntity.ok(ponto);

        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    // Página inicial -> filtros
    //@GetMapping
    //public ResponseEntity<List<PontoInteresse>> getFiltered(
    //        @RequestParam String userCoordinates,
     //       @RequestParam(required = false, defaultValue = "10") int distancia,
      //      @RequestParam(required = false) Double score,
       //     @RequestParam(required = false) String date,
        //    @RequestParam(required = false) Integer visitantes,
         //   @RequestParam(required = false) Boolean acessibilidade,
          //  @RequestParam(required = false) Integer dificuldade
    //) {
     //   LocalDateTime dataFinal = (date != null) ? LocalDateTime.parse(date) : null;

        //List<PontoInteresse> resultado = pontoInteresseService.getNewest(
         //       userCoordinates,
          //      distancia,
           //     (score != null) ? score : 0.0,
            //    dataFinal,
             //   (visitantes != null) ? visitantes : 0,
              //  (acessibilidade != null) ? acessibilidade : false,
               // (dificuldade != null) ? dificuldade : 0
        //);
        //return ResponseEntity.ok(resultado);
    //}

}
