package pt.uminho.di.aa.miradourum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;
import pt.uminho.di.aa.miradourum.models.Review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PontoInteresseRepository extends JpaRepository<PontoInteresse, Long> {
    @Query("SELECT p FROM PontoInteresse p WHERE p.state = true")
    List<PontoInteresse> findByState();

    @Query("""
    SELECT p FROM PontoInteresse p 
    WHERE (:score IS NULL OR p.score >= :score)
      AND (:date IS NULL OR p.creationDate >= :date)
      AND (:accessibility IS NULL OR p.accessibility = :accessibility)
      AND (:difficulty IS NULL OR p.difficulty = :difficulty)
      AND (:visitantes IS NULL OR 
           (SELECT COUNT(u) FROM User u JOIN u.pontoInteresse pi WHERE pi = p) >= :visitantes)
      """)
    List<PontoInteresse> findFiltered(
            @Param("score") Double score,
            @Param("date") LocalDateTime date,
            @Param("accessibility") Boolean accessibility,
            @Param("difficulty") Integer difficulty,
            @Param("visitantes") Integer visitantes
    );

    @Query("SELECT r FROM Review r WHERE r.pontoInteresse = :pontoInteresse")
    List<Review> findReviews(@Param("pontoInteresse") PontoInteresse pontoInteresse);

}