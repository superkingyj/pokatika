package pokatika.example.pokatika.nft;

import lombok.*;
import pokatika.example.pokatika.event.Event;
import pokatika.example.pokatika.participants.Participants;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Nft {
    @Id
    @GeneratedValue
    private Long id;
    private String tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "nft")
    @JoinColumn(name = "participants_id")
    private Participants participants;
}
