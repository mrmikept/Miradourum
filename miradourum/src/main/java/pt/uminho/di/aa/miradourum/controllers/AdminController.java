package pt.uminho.di.aa.miradourum.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final JwtService jwtService;
    private final UserService userService;
    private final PontoInteresseService pontoInteresseService;

    public AdminController(JwtService jwtService, UserService userService, PontoInteresseService pontoInteresseService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.pontoInteresseService = pontoInteresseService;
    }

    //retorna todos os pontos de interesse que estão inativos
    @GetMapping("/pi")
    public ResponseEntity<?> getInactive(@RequestHeader("Authorization") String authHeader) {
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

            // 3. getRole
            Integer role = jwtService.extractRole(token);
            if (role != 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not admin");
            }

            // 4. Fetch all inactive interest points
            List<PontoInteresse> pontosInativos = pontoInteresseService.getInactive();
            return ResponseEntity.ok(pontosInativos);


        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }

    //altera o estado de um ponto de interesse para ativo
    @PutMapping("/pi/{id}")
    public ResponseEntity<?> activatePoint(@PathVariable Long id,@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        try {
            // 2. Check if token is expired
            if (jwtService.tokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
            }

            // 3. getRole
            Integer role = jwtService.extractRole(token);
            if (role != 2) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not admin");
            }

            // get the wanted point

            PontoInteresse ponto = pontoInteresseService.getByIdComplete(id);
            ponto.setState(true);
            pontoInteresseService.savePontoInteresse(ponto);
            return ResponseEntity.ok(ponto);


        } catch (Exception e) {
            // Handle token-related exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
        }
    }
}
