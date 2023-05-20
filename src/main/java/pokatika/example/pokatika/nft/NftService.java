package pokatika.example.pokatika.nft;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pokatika.example.pokatika.common.ApiException;
import pokatika.example.pokatika.event.Event;
import pokatika.example.pokatika.event.EventRepository;
import pokatika.example.pokatika.participants.Participants;
import pokatika.example.pokatika.participants.ParticipantsRepository;

import static pokatika.example.pokatika.common.StatusMessage.EVENT_NOT_FOUND;
import static pokatika.example.pokatika.common.StatusMessage.PARTICIPANT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NftService {
    private final NftRepository nftRepository;
    private final EventRepository eventRepository;
    private final ParticipantsRepository participantsRepository;

    /**
     * 월렛 주소, NFT 토큰 아이디, 이벤트 아이디를 받아 저장
     * @param walletAddress 월렛 주소
     * @param nftId NFT 토큰 아이디
     * @param eventId 이벤트 아이디
     */
    public void save(String walletAddress, String nftId, Long eventId){
        Participants participants = participantsRepository.findParticipantsByWalletAddress(walletAddress)
                .orElseThrow(() -> new ApiException(PARTICIPANT_NOT_FOUND));

        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new ApiException(EVENT_NOT_FOUND));

        Nft nft = Nft.builder()
                    .event(event)
                    .participants(participants)
                    .tokenId(nftId).build();

        nftRepository.save(nft);
    }



}
