package pt.uminho.di.aa.miradourum.controllers;


import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.auth.JwtService;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.User;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;
import pt.uminho.di.aa.miradourum.services.UserService;
import pt.uminho.di.aa.miradourum.utils.EmailService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final JwtService jwtService;
    private final UserService userService;
    private final PontoInteresseService pontoInteresseService;
    private final EmailService emailService;

    public AdminController(JwtService jwtService, UserService userService, PontoInteresseService pontoInteresseService,EmailService emailService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.pontoInteresseService = pontoInteresseService;
        this.emailService = emailService;
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
    public ResponseEntity<?> activatePoint(@PathVariable Long id,@RequestHeader("Authorization") String authHeader,@RequestBody Map<String, Object> body) {
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


            Optional<PontoInteresse> optionalPonto = pontoInteresseService.getByIdComplete(id);
            if (optionalPonto.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ponto de interesse not found");
            }

            PontoInteresse ponto = optionalPonto.get();

            if(ponto.getState()==true){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ponto de interesse already active");
            }

            Boolean accepted = (Boolean) body.get("accepted");
            String comment = null;
            if (accepted == null) {
                return ResponseEntity.badRequest().body("Missing 'state' field");
            }

            //state false means admim did not accept the pi
            if(!accepted){
                comment = (String) body.get("comment");
                if(comment==null){
                    return ResponseEntity.badRequest().body("Missing 'comment' field");
                }
            }

            if(accepted==true){
                // get the wanted point
                ponto.setState(true);
                pontoInteresseService.savePontoInteresse(ponto);
                return ResponseEntity.ok().body("Ponto interesse activated!");
            }
            else if(accepted==false){
                pontoInteresseService.deletePontoInteresse(ponto);;
                emailService.sendEmail(ponto.getCreatorEmail(),comment);
                return ResponseEntity.ok().body("Ponto interesse removed successfully and email sent to user.");
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or tampered token");
            }

        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        }
        catch (Exception e) {
            // Handle token-related exceptions
            e.printStackTrace(); // add this to log the actual error
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mano, se o flow chegou aqui é mau sinal");
    }

}
