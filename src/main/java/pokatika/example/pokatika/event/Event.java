package pokatika.example.pokatika.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pokatika.example.pokatika.nft.Nft;
import pokatika.example.pokatika.participants.Participants;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue
    private Long id;
    private Long count;
    private String eventTitle;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String address;

    @Lob
    private byte[] image;
    @OneToMany(mappedBy = "event")
    private List<Participants> participantsList = new ArrayList<>();
    @OneToMany(mappedBy = "event")
    private List<Nft> nftList = new ArrayList<>();
}
