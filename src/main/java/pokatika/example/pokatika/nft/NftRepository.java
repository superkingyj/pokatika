package pokatika.example.pokatika.nft;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NftRepository extends JpaRepository<Nft, Long> {
    Optional<Nft> findByTokenId(String tokenId);
}
