package pt.uminho.di.aa.miradourum.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.uminho.di.aa.miradourum.auth.AuthService;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
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
    public ResponseEntity<Object> registerUser(@RequestBody Map<String,String> userInfo, HttpServletResponse response) {

        try{
            String email = userInfo.get("email");
            String password = userInfo.get("password");
            String username = userInfo.get("username");

            //Todo add image here

            User created = authService.register(email,username,password,1);

            if(created==null){
                return ResponseEntity.status(401).body("Email already exists");
            }
            else{
                return ResponseEntity.status(200).body("User registered successfully");
            }

        }
        catch(Exception ex){
            return ResponseEntity.status(401).body(ex.getMessage());
        }

    }


}
