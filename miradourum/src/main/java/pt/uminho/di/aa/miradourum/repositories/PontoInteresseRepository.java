package pt.uminho.di.aa.miradourum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;
import pt.uminho.di.aa.miradourum.dtos.PontoInteresse.PIDetailsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PontoInteresseRepository extends JpaRepository<PontoInteresse, Long> {
    @Query("SELECT p FROM PontoInteresse p WHERE p.state = true")
    List<PontoInteresse> findByState();

    @Query("SELECT p FROM PontoInteresse p WHERE p.state = false")
    List<PontoInteresse> findIncative();

    @Query("""
        SELECT p FROM PontoInteresse p 
        WHERE (:score IS NULL OR p.score >= :score)
           AND (:date IS NULL OR p.creationDate >= :date)
           AND (:accessibility IS NULL OR p.accessibility = :accessibility)
           AND (:difficulty IS NULL OR p.difficulty = :difficulty)
           AND (:visitantes IS NULL OR 
            (SELECT COUNT(u) FROM User u JOIN u.pontoInteresse pi WHERE pi = p) >= :visitantes)
           AND p.latitude BETWEEN :minLat AND :maxLat
           AND p.longitude BETWEEN :minLon AND :maxLon
    """)
    List<PontoInteresse> findFilteredWithBox(
            @Param("score") Double score,
            @Param("date") LocalDateTime date,
            @Param("accessibility") Boolean accessibility,
            @Param("difficulty") Integer difficulty,
            @Param("visitantes") Integer visitantes,
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLon") Double minLon,
            @Param("maxLon") Double maxLon
    );

    @Query("SELECT r FROM Review r WHERE r.pontoInteresse = :pontoInteresse")
    List<Review> findReviews(@Param("pontoInteresse") PontoInteresse pontoInteresse);

    // Correct - no space between colon and parameter
    @Query("SELECT new pt.uminho.di.aa.miradourum.dtos.PontoInteresse.PIDetailsDto(pi.latitude, pi.longitude, pi.name, pi.description, pi.difficulty, pi.accessibility, pi.premium, pi.score, pi.creationDate) FROM PontoInteresse pi WHERE pi.id = :id")
    PIDetailsDto getFromId(@Param("id") Long id);
}