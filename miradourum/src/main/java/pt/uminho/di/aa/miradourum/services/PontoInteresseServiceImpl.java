package pt.uminho.di.aa.miradourum.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.repositories.PontoInteresseRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PontoInteresseServiceImpl implements PontoInteresseService {

    @Autowired
    private PontoInteresseRepository pontoInteresseRepository;

    @Override
    public List<PontoInteresse> getNewest(String userCoordinates, int distancia, double score, LocalDateTime date, int visitantes, boolean acessibilidade, int difficulty) {

        List<PontoInteresse> preFiltrados = pontoInteresseRepository.findFiltered(
                score, date, acessibilidade, difficulty, visitantes
        );
        //Todo falta add o filtro das coordenadas
        return preFiltrados;
    }

    @Override
    public PontoInteresse getById(Long pontoInteresseId) {
        return pontoInteresseRepository.findById(pontoInteresseId).orElse(null);
    }

    @Override
    public void savePontoInteresse(PontoInteresse pontoInteresse) {
        pontoInteresseRepository.save(pontoInteresse);
    }

    @Override
    public List<PontoInteresse> getByState() {
        return pontoInteresseRepository.findByState();
    }

    @Override
    public List<Review> getReviews(PontoInteresse pontoInteresse) {
        return pontoInteresseRepository.findReviews(pontoInteresse);
    }
}
