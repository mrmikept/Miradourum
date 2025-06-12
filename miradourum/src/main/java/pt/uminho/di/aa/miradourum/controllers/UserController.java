package pt.uminho.di.aa.miradourum.controllers;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.dto.PaymentDTO;
import pt.uminho.di.aa.miradourum.dto.PaymentResponseDTO;
import pt.uminho.di.aa.miradourum.dto.UpdateUserDTO;
import pt.uminho.di.aa.miradourum.models.Image;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.projections.User.UserEditProfileProjection;
import pt.uminho.di.aa.miradourum.projections.User.UserProfileProjection;
import pt.uminho.di.aa.miradourum.services.ReviewService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @GetMapping(value = {"/{id}", ""})
    public ResponseEntity<?> getUserProfile(
            @PathVariable(required = false) Long id,
            @RequestHeader("Authorization") String authHeader) {

        // Validar token
        ResponseEntity<?> tokenValidation = jwtService.validateToken(authHeader);
        if (tokenValidation != null) {
            return tokenValidation;
        }

        Long targetUserId;

        // Se não foi fornecido ID, usa o do token (próprio perfil)
        if (id == null) {
            targetUserId = jwtService.extractUserIdFromValidToken(authHeader);
        } else {
            targetUserId = id;
        }

        UserProfileProjection user = userService.getUserById(targetUserId, UserProfileProjection.class);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    // Get User Edit Profile
    @GetMapping("/edit")  // Remove o {id} do path
    public ResponseEntity<?> getEditUser(@RequestHeader("Authorization") String authHeader) {
        // Validar token
        ResponseEntity<?> tokenValidation = jwtService.validateToken(authHeader);
        if (tokenValidation != null) {
            return tokenValidation;
        }

        // Extrair userId do token válido
        Long userId = jwtService.extractUserIdFromValidToken(authHeader);

        UserEditProfileProjection user = userService.getUserById(userId, UserEditProfileProjection.class);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    // Update user
    @PutMapping("/profile")
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UpdateUserDTO userDTO,
                                             BindingResult bindingResult,
                                             @RequestHeader("Authorization") String authHeader) {

        // Validar token
        ResponseEntity<?> tokenValidation = jwtService.validateToken(authHeader);
        if (tokenValidation != null) {
            return (ResponseEntity<Object>) tokenValidation;
        }

        // Extrair userId do token válido
        Long userId = jwtService.extractUserIdFromValidToken(authHeader);

        // Verificar erros de validação do DTO
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error ->
                    errorMessage.append(error.getDefaultMessage()).append("; ")
            );
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }

        // Verificar se pelo menos um campo foi fornecido
        if (!userDTO.hasAtLeastOneField()) {
            return ResponseEntity.badRequest().body("At least one field (username, password, or profileImage) must be provided.");
        }

        // Buscar e verificar se user existe
        User existingUser = userService.getUserById(userId, User.class);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Atualizar user
        userService.saveAndUpdateUser(
                userId,
                userDTO.getUsername(),
                userDTO.getPassword(),
                userDTO.getProfileImage()
        );

        // Retornar resposta com dados seguros do utilizador
        User updatedUser = userService.getUserById(userId, User.class);
        Map<String, Object> safeUser = Map.of(
                "username", updatedUser.getUsername(),
                "email", updatedUser.getEmail(),
                "profileImage", updatedUser.getProfileImage() != null ? updatedUser.getProfileImage() : ""
        );

        return ResponseEntity.ok(safeUser);
    }

    // Check if user is premium
    @GetMapping("/premium")
    public ResponseEntity<?> isPremium(@RequestHeader("Authorization") String authHeader) {
        // Validar token
        ResponseEntity<?> tokenValidation = jwtService.validateToken(authHeader);
        if (tokenValidation != null) {
            return (ResponseEntity<Object>) tokenValidation;
        }

        // Extrair userId do token válido
        Long userId = jwtService.extractUserIdFromValidToken(authHeader);

        return ResponseEntity.ok(userService.checkPremium(userId));
    }

    // Get image URLs dos pontos visitados
    @GetMapping("/images")
    public ResponseEntity<?> getImageUrls(@RequestHeader("Authorization") String authHeader) {

        // Validar token
        ResponseEntity<?> tokenValidation = jwtService.validateToken(authHeader);
        if (tokenValidation != null) {
            return tokenValidation;
        }

        // Extrair userId do token válido
        Long userId = jwtService.extractUserIdFromValidToken(authHeader);

        List<Review> reviews = reviewService.getAllReviewsUser(userId);
        List<Image> images = new ArrayList<Image>();
        for (Review review : reviews) {
            images.addAll(review.getImages());
        }
        return ResponseEntity.ok(images);
    }

    // Get pontos do utilizador
    @GetMapping("/pontos")
    public ResponseEntity<?> getPontos(@RequestHeader("Authorization") String authHeader) {

        // Validar token
        ResponseEntity<?> tokenValidation = jwtService.validateToken(authHeader);
        if (tokenValidation != null) {
            return tokenValidation;
        }

        // Extrair userId do token válido
        Long userId = jwtService.extractUserIdFromValidToken(authHeader);

        User u = userService.getUserById(userId, User.class);
        return ResponseEntity.ok(u.getPontoInteresse());
    }

    @PostMapping("/upgrade-premium")
    public ResponseEntity<?> upgradeToPremium(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody PaymentDTO paymentData) {

        // Validar token
        ResponseEntity<?> tokenValidation = jwtService.validateToken(authHeader);
        if (tokenValidation != null) {
            return tokenValidation;
        }

        // Extrair userId do token válido
        Long userId = jwtService.extractUserIdFromValidToken(authHeader);

        // Verificar se utilizador existe e tem role=1
        User user = userService.getUserById(userId, User.class);
        if (user.getRole() != 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User is not eligible for premium upgrade");
        }

        // Chamar microsserviço de pagamento
        PaymentResponseDTO paymentResponse = callPaymentService(paymentData);

        if (!paymentResponse.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Payment failed: " + paymentResponse.getError());
        }

        // Atualizar utilizador para role=3 e definir data de expiração
        try {
            String isoDate = paymentResponse.getExpiryDate().replace("Z", "");
            LocalDateTime localDateTime = LocalDateTime.parse(isoDate);

            // Converter LocalDateTime para Date se necessário
            Date expiryDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

            user.setRole(3);
            user.setPremiumEndDate(expiryDate);
            userService.saveUserWithoutPasswordEncoding(user);

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