package pt.uminho.di.aa.miradourum.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.services.PontoInteresseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pi")
public class PontoInteresseController {

    private final PontoInteresseService pontoInteresseService;

    public PontoInteresseController(PontoInteresseService pontoInteresseService) {
        this.pontoInteresseService = pontoInteresseService;
    }

    // Obter ponto de interesse e seus detalhes
    @GetMapping("/{id}/detalhes")
    public ResponseEntity<?> getDetalhesPontoComReviews(@PathVariable Long id) {
        PontoInteresse ponto = pontoInteresseService.getById(id);
        if (ponto == null) return ResponseEntity.notFound().build();
        List<Review> reviews = pontoInteresseService.getReviews(ponto);

        Map<String, Object> response = new HashMap<>();
        response.put("ponto", ponto);
        response.put("reviews", reviews);

        return ResponseEntity.ok(response);
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
