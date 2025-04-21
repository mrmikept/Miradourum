package pt.uminho.di.aa.miradourum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.uminho.di.aa.miradourum.models.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
}
