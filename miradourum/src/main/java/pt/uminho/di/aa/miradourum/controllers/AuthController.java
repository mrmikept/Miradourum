package pt.uminho.di.aa.miradourum.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.uminho.di.aa.miradourum.auth.AuthService;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService,JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String,String> credentials, HttpServletResponse response) {
        try{
            String email = credentials.get("email");
            String password = credentials.get("password");

            String token = authService.login(email,password);

            if (token == null) {
                return ResponseEntity.status(401).body("Invalid email or password");
            }

            return ResponseEntity.ok(token); // JWT token returned if login successful
        }
        catch(Exception ex){
        return ResponseEntity.status(401).body(ex.getMessage());}
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody Map<String, String> userInfo, HttpServletResponse response) {
        try {
            String email = userInfo.get("email");
            String password = userInfo.get("password");
            String username = userInfo.get("username");
            String profileimage = userInfo.get("profileimage");

            // Validate mandatory fields
            if (email == null || email.trim().isEmpty() ||
                    password == null || password.trim().isEmpty() ||
                    username == null || username.trim().isEmpty() ||
                    profileimage == null || profileimage.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("All fields (email, password, username, profileimage) are required.");
            }

            User created = authService.register(email, username, password, 1,profileimage);

            if (created == null) {
                return ResponseEntity.status(401).body("Email already exists");
            } else {
                return ResponseEntity.ok("User registered successfully");
            }

        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error: " + ex.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Object> updateProfile(@RequestHeader("Authorization") String authHeader,
                                                @RequestBody Map<String, String> userInfo) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7);

        try {
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            Long userId = jwtService.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            // Extract fields if provided
            String newUsername = userInfo.get("username");
            String newPassword = userInfo.get("password");
            String newProfileImage = userInfo.get("profileimage");

            // Check if at least one field is provided
            if ((newUsername == null || newUsername.trim().isEmpty()) &&
                    (newPassword == null || newPassword.trim().isEmpty()) &&
                    (newProfileImage == null || newProfileImage.trim().isEmpty())) {
                return ResponseEntity.badRequest().body("At least one field (username, password, or profileimage) must be provided.");
            }

            // Call service method to update only the fields provided
            User user = authService.updateProfile(userId, newUsername, newPassword, newProfileImage);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            Map<String, Object> safeUser = Map.of(
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "profileimage", user.getProfileImage()
            );

            return ResponseEntity.ok(safeUser);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }



}
