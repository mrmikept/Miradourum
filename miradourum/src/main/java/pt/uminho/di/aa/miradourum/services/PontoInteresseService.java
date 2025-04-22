package pt.uminho.di.aa.miradourum.services;

import pt.uminho.di.aa.miradourum.dtos.PontoInteresse.PIDetailsDto;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;

import java.time.LocalDateTime;
import java.util.List;

public interface PontoInteresseService {

    List<PontoInteresse> getNewest(String userCoordinates, int distancia, double score, LocalDateTime date, int visitantes, boolean acessibilidade, int difficulty);

    PIDetailsDto getById(Long pontoInteresseId);

    void savePontoInteresse(PontoInteresse pontoInteresse);

    List<PontoInteresse> getByState();

    List<Review> getReviews(PontoInteresse pontoInteresse);

    public List<PontoInteresse> getInactive();
}