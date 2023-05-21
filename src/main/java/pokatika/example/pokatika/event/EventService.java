package pokatika.example.pokatika.event;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class EventService {

    private final ParticipantsRepository participantsRepository;
    private final EventRepository eventRepository;
    private final NftRepository nftRepository;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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

        validateFileExists(event.getImageUrl());

//        remakeImage(event.getImageUrl());

        return EventInfoByWalletDto.builder()
                .walletAddress(participants.getWalletAddress())
                .twitterHandle(participants.getTwitterHandle())
                .count(event.getCount())
                .eventId(event.getId())
                .startDate(event.getStartDate().format(DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")))
                .endDate(event.getEndDate().format(DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")))
                .eventTitle(event.getEventTitle())
                .address(event.getAddress())
                .nftImage(null)
                .build();
    }

    /**
     * NFT 토큰 아이디를 받아 이벤트 정보를 넘겨줌
     * @param nftTokenId NFT 토큰 아이디
     * @return EventInfoByNftDto
     */
    public EventInfoByNftDto getEventInfoByNft(String nftTokenId){
        Nft nft = nftRepository.findByTokenId(nftTokenId)
                .orElseThrow(() -> new ApiException(NFT_NOT_FOUND));

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

    // 아직 짜는 중
    private byte[] remakeImage(String fileName) {
        S3Object s3Object = amazonS3.getObject(bucket, fileName);
        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

//        try{
//            return IOUtils.toByteArray(s3ObjectInputStream);
//        }catch (IOException e){
//
//        }
        return new byte[]{};

    }

    private void validateFileExists(String fileName) throws ApiException {
        if(amazonS3.doesObjectExist(bucket, fileName)){
            throw new ApiException(IMAGE_NOT_FOUND);
        }
    }
}
