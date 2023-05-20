package pokatika.example.pokatika.participants;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantsRepository extends JpaRepository<Participants, Long> {
    Optional<Participants> findParticipantsByWalletAddress(String walletAddress);
}
