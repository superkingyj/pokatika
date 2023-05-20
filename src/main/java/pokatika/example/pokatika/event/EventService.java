package pokatika.example.pokatika.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pokatika.example.pokatika.common.ApiException;
import pokatika.example.pokatika.event.dto.EventInfoByNftDto;
import pokatika.example.pokatika.event.dto.EventInfoByWalletDto;
import pokatika.example.pokatika.nft.Nft;
import pokatika.example.pokatika.nft.NftRepository;
import pokatika.example.pokatika.participants.Participants;
import pokatika.example.pokatika.participants.ParticipantsRepository;


import java.time.format.DateTimeFormatter;

import static pokatika.example.pokatika.common.StatusMessage.*;

@Service
@RequiredArgsConstructor
public class EventService {

    private final ParticipantsRepository participantsRepository;
    private final EventRepository eventRepository;
    private final NftRepository nftRepository;

    /**
     * 월렛 주소를 입력받아 해당하는 지갑 주소, 이벤트 이미지, 순번을 넘겨줌
     * @param walletAddress 지갑 주소
     * @return EventInfoByWalletDto
     */
    public EventInfoByWalletDto getEventInfoByWallet(String walletAddress) {
        Participants participants = participantsRepository.findParticipantsByWalletAddress(walletAddress)
                .orElseThrow(() -> new ApiException(PARTICIPANT_NOT_FOUND));

        Event event = eventRepository.findEventById(participants.getEvent().getId())
                .orElseThrow(() -> new ApiException(EVENT_NOT_FOUND));

        eventRepository.updateCount(event.getId());
        byte[] remakeImage = remakeImage(event.getImage());

        return EventInfoByWalletDto.builder()
                .walletAddress(participants.getWalletAddress())
                .twitterHandle(participants.getTwitterHandle())
                .count(event.getCount())
                .eventId(event.getId())
                .startDate(event.getStartDate().format(DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")))
                .endDate(event.getEndDate().format(DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")))
                .eventTitle(event.getEventTitle())
                .address(event.getAddress())
                .nftImage(remakeImage)
                .build();
    }

    /**
     * NFT 토큰 아이디를 받아 이벤트 정보를 넘겨줌
     * @param nftTokenId NFT 토큰 아이디
     * @return EventInfoByNftDto
     */
    public EventInfoByNftDto getEventInfoByNft(String nftTokenId){
        Nft nft = nftRepository.findByTokenId(nftTokenId)
                .orElseThrow(() -> new ApiException(NFT_NOF_FOUND));

        Event event = eventRepository.findEventById(nft.getEvent().getId())
                .orElseThrow(() -> new ApiException(EVENT_NOT_FOUND));

        return EventInfoByNftDto.builder()
                .eventId(event.getId())
                .startDate(event.getStartDate().format(DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")))
                .endDate(event.getEndDate().format(DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")))
                .eventTitle(event.getEventTitle())
                .address(event.getAddress())
                .build();
    }

    private byte[] remakeImage(byte[] image){
        byte[] remakeImage = new byte[]{1, 2, 3};
        return remakeImage;
    }
}
