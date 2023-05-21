package pokatika.example.pokatika.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventById(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Event e SET e.count = (e.count+1) WHERE e.id = :id")
    void updateCount(@Param("id") Long id);
    boolean existsById(Long id);
}
