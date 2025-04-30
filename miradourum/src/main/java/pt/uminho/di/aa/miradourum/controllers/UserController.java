package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.projections.User.UserEditProfileProjection;
import pt.uminho.di.aa.miradourum.projections.User.UserProfileProjection;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PontoInteresseService pontoInteresseService;
    private final JwtService jwtService;
    public UserController(UserService userService, PontoInteresseService pontoInteresseService, JwtService jwtService) {
        this.userService = userService;
        this.pontoInteresseService = pontoInteresseService;
        this.jwtService = jwtService;
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
            String email = jwtService.extractEmail(token);
            String password = credentials.get("password");
            String image = credentials.get("profileImage");

            userService.saveAndUpdateUser(username,email,password,image);
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
    @GetMapping("/{id}/images")
    public ResponseEntity<List<String>> getImageUrls(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getImagesURL(id));
    }

}
