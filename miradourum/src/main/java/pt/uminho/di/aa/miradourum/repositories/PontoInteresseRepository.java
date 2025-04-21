package pt.uminho.di.aa.miradourum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pt.uminho.di.aa.miradourum.models.PontoInteresse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PontoInteresseRepository extends JpaRepository<PontoInteresse, Long> {
    @Query("SELECT p FROM PontoInteresse p WHERE p.state = true")
    List<PontoInteresse> findByState();

    @Query("""
    SELECT p FROM PontoInteresse p 
    WHERE p.score >= :score
      AND p.creationDate >= :date
      AND p.accessibility = :accessibility
      AND p.difficulty = :difficulty
      AND (SELECT COUNT(u) FROM User u JOIN u.pontoInteresse pi WHERE pi = p) >= :visitantes
      """)
    List<PontoInteresse> findFiltered(
            @Param("score") double score,
            @Param("date") LocalDateTime date,
            @Param("accessibility") Boolean acessibilidade,
            @Param("difficulty") int difficulty,
            @Param("visitantes") int visitantes
    );

}