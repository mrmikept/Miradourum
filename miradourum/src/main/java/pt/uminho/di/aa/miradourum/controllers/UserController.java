package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PontoInteresseService pontoInteresseService;

    public UserController(UserService userService, PontoInteresseService pontoInteresseService) {
        this.userService = userService;
        this.pontoInteresseService = pontoInteresseService;
    }
    // Get all users
    @GetMapping
    public ResponseEntity<List<PontoInteresse>> getAllUsers() {
        return ResponseEntity.ok(pontoInteresseService.getNewest("merda",123,12.3, LocalDateTime.now(),12,false,1));
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
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
    @PutMapping("/{id}/update")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User existingUser = userService.getUserById(id.longValue());
        if (existingUser == null) return ResponseEntity.notFound().build();

        updatedUser.setId(id);
        userService.saveAndUpdateUser(updatedUser);
        return ResponseEntity.ok("User updated successfully");
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
