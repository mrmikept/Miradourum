package pt.uminho.di.aa.miradourum.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.uminho.di.aa.miradourum.dtos.PontoInteresse.PIDetailsDto;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.repositories.PontoInteresseRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PontoInteresseServiceImpl implements PontoInteresseService {

    @Autowired
    private PontoInteresseRepository pontoInteresseRepository;

    @Override
    public List<PontoInteresse> getNewest(String userCoordinates, int distancia, double score, LocalDateTime date, int visitantes, boolean acessibilidade, int difficulty) {

        String[] parts = userCoordinates.split(",");
        double userLat = Double.parseDouble(parts[0]);
        double userLon = Double.parseDouble(parts[1]);

        double latDistance = distancia / 111.0;
        double lonDistance = distancia / (111.0 * Math.cos(Math.toRadians(userLat)));

        double minLat = userLat - latDistance;
        double maxLat = userLat + latDistance;
        double minLon = userLon - lonDistance;
        double maxLon = userLon + lonDistance;

        List<PontoInteresse> preFiltrados = pontoInteresseRepository.findFilteredWithBox(
                score, date, acessibilidade, difficulty, visitantes,
                minLat, maxLat, minLon, maxLon
        );

        // filtro de distância
        return preFiltrados.stream()
                .filter(p -> haversine(userLat, userLon, p.getLatitude(), p.getLongitude()) <= distancia)
                .toList();
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public PIDetailsDto getById(Long pontoInteresseId) {
        PIDetailsDto pi = pontoInteresseRepository.getFromId(pontoInteresseId);
        if (pi == null)
            return null;

        return pi;
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
    public List<PontoInteresse> getInactive() {return pontoInteresseRepository.findIncative();}


    @Override
    public List<Review> getReviews(PontoInteresse pontoInteresse) {
        return pontoInteresseRepository.findReviews(pontoInteresse);
    }
}
