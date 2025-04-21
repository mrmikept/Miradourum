package pt.uminho.di.aa.miradourum.services;

import pt.uminho.di.aa.miradourum.models.PontoInteresse;

import java.time.LocalDateTime;
import java.util.List;

public interface PontoInteresseService {

    List<PontoInteresse> getNewest(String userCoordinates, int distancia, double score, LocalDateTime date, int visitantes, boolean acessibilidade, int difficulty);

    PontoInteresse getById(Long pontoInteresseId);

    void savePontoInteresse(PontoInteresse pontoInteresse);

    List<PontoInteresse> getByState();
}