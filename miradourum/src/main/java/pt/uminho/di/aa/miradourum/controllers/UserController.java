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

}
