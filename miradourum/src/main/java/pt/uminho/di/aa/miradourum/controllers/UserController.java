package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.dto.PaymentDTO;
import pt.uminho.di.aa.miradourum.dto.PaymentResponseDTO;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.projections.User.UserEditProfileProjection;
import pt.uminho.di.aa.miradourum.projections.User.UserProfileProjection;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.ReviewService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final ReviewService reviewService;

    public UserController(UserService userService, JwtService jwtService, ReviewService reviewService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.reviewService = reviewService;
    }

    // Get User Profile
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        // 1. Check if token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }
        if (id == null) {
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }

        UserProfileProjection user = userService.getUserById(id, UserProfileProjection.class);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    // Get User Edit Profile
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> getEditUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {

        // 1. Check if token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }
        if (id == null) {
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
            if(!userId.equals(id)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Can't acess other user info");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }

        UserEditProfileProjection user = userService.getUserById(id, UserEditProfileProjection.class);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    // Create new user Post("/register")
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        userService.saveUser(user);
        return ResponseEntity.ok("User created successfully");
    }

    // Update user
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody Map<String, String> credentials, @RequestHeader("Authorization") String authHeader) {
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

            // 4. Fetch user and update
            User existingUser = userService.getUserById(userId, User.class);
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            String username = credentials.get("username");
            String password = credentials.get("password");
            String image = credentials.get("profileImage");

            userService.saveAndUpdateUser(userId,username,password,image);
            return ResponseEntity.ok("User updated successfully");

        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    // Check if user is premium
    @GetMapping("/{id}/premium")
    public ResponseEntity<Boolean> isPremium(@PathVariable Long id) {
        return ResponseEntity.ok(userService.checkPremium(id));
    }

    // Get image URLs dos pontos visitados
    @GetMapping("/images")
    public ResponseEntity<?> getImageUrls(@RequestHeader("Authorization") String authHeader) {


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


        List<Review> reviews = reviewService.getAllReviewsUser(userId);
        List<Image> images = new ArrayList<Image>();
        for (Review review : reviews) {
            images.addAll(review.getImages());
        }
        return ResponseEntity.ok(images);
    }
        catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }
    // Get image URLs dos pontos visitados
    @GetMapping("/pontos")
    public ResponseEntity<?> getPontos(@RequestHeader("Authorization") String authHeader) {


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

            User u = userService.getUserById(userId, User.class);

            return ResponseEntity.ok(u.getPontoInteresse());
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    @PostMapping("/upgrade-premium")
    public ResponseEntity<?> upgradeToPremium(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PaymentDTO paymentData) {

        // 1. Verificar token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7);

        try {
            // 2. Verificar se token está expirado
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            // 3. Extrair user ID
            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            // 4. Verificar se utilizador existe e tem role=1
            User user= userService.getUserById(userId, User.class);
            if (user.getRole() != 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User is not eligible for premium upgrade");
            }

            // 5. Chamar microsserviço de pagamento
            PaymentResponseDTO paymentResponse = callPaymentService(paymentData);

            if (!paymentResponse.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Payment failed: " + paymentResponse.getError());
            }

            // 6. Atualizar utilizador para role=3 e definir data de expiração
            try {
                String isoDate = paymentResponse.getExpiryDate().replace("Z", "");
                LocalDateTime localDateTime = LocalDateTime.parse(isoDate);

                // Converter LocalDateTime para Date se necessário
                Date expiryDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                user.setRole(3);
                user.setPremiumEndDate(expiryDate);
                userService.saveUser(user);

                return ResponseEntity.ok().body(Map.of(
                        "success", true,
                        "message", "Premium upgrade successful",
                        "expiryDate", expiryDate,
                        "newRole", 3
                ));

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to update user: " + e.getMessage());
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    private PaymentResponseDTO callPaymentService(PaymentDTO paymentData) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String paymentServiceUrl = "http://localhost:3000/api/pay";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PaymentDTO> request = new HttpEntity<>(paymentData, headers);

            ResponseEntity<PaymentResponseDTO> response = restTemplate.postForEntity(
                    paymentServiceUrl,
                    request,
                    PaymentResponseDTO.class
            );

            return response.getBody();

        } catch (Exception e) {
            PaymentResponseDTO errorResponse = new PaymentResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setError("Payment service unavailable: " + e.getMessage());
            return errorResponse;
        }
    }

}

