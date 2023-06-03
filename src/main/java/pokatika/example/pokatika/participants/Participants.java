package pokatika.example.pokatika.participants;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import pokatika.example.pokatika.event.Event;
import pokatika.example.pokatika.nft.Nft;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Participants {
    @Id
    @GeneratedValue
    private Long id;
    private String twitterHandle;
    private String walletAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nft_id")
    private Nft nft;

    @JoinColumn(name = "is_rsvp")
    @ColumnDefault("0")
    private Boolean isRsvp;

    public void setNft(Nft nft){
        this.nft = nft;
    }
}
