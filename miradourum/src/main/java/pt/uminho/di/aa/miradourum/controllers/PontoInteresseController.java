package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.dtos.PontoInteresse.PIDetailsDto;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pi")
public class PontoInteresseController {

    private final PontoInteresseService pontoInteresseService;
    private final UserService userService;
    private final JwtService jwtService;

    public PontoInteresseController(PontoInteresseService pontoInteresseService, JwtService jwtService, UserService userService) {
        this.pontoInteresseService = pontoInteresseService;
        this.jwtService = jwtService;
        this.userService = userService;
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
