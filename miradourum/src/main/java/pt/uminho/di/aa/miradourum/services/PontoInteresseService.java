package pt.uminho.di.aa.miradourum.services;

import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.models.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PontoInteresseService {

    List<PontoInteresse> getNewest(String userCoordinates, int distancia, double score, LocalDateTime date, int visitantes, boolean acessibilidade, int difficulty);

    <T> T getById(Long pontoInteresseId, Class<T> clazz);

    public Optional<PontoInteresse> getByIdComplete(Long pontoInteresseId);

    void savePontoInteresse(PontoInteresse pontoInteresse);

    List<PontoInteresse> getByState();

    List<Review> getReviews(PontoInteresse pontoInteresse);

    public List<PontoInteresse> getInactive();

    public void deletePontoInteresse(PontoInteresse pontoInteresse);
}