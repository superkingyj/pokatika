package pokatika.example.pokatika.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pokatika.example.pokatika.common.ApiException;
import pokatika.example.pokatika.event.dto.EventInfoByNftDto;
import pokatika.example.pokatika.event.dto.EventInfoByWalletDto;
import pokatika.example.pokatika.event.dto.IpfsResponse;
import pokatika.example.pokatika.nft.Nft;
import pokatika.example.pokatika.nft.NftRepository;
import pokatika.example.pokatika.participants.Participants;
import pokatika.example.pokatika.participants.ParticipantsRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.format.DateTimeFormatter;

import static pokatika.example.pokatika.common.StatusMessage.*;

@Slf4j
@Service
public class EventService {
    private final ParticipantsRepository participantsRepository;
    private final EventRepository eventRepository;
    private final NftRepository nftRepository;
    @Value("${ipfs.api}")
    private String IPFS_API;
    @Value("${ipfs.key}")
    private String KEY;
    @Value("${ipfs.gateway}")
    private String GATEWAY;

    private static Font FONT;
    private static int X = 10;
    private static int Y = 25;
    private static Color COLOR = Color.black;

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EventService(ParticipantsRepository participantsRepository, EventRepository eventRepository, NftRepository nftRepository) {
        this.participantsRepository = participantsRepository;
        this.eventRepository = eventRepository;
        this.nftRepository = nftRepository;
    }

    /**
     * NFT 이미지 가져오기: 월렛 주소를 입력받아 해당하는 지갑 주소, 트위터 핸들이 박힌 이벤트 이미지, 순번을 넘겨줌
     * @param walletAddress 지갑 주소
     * @return EventInfoByWalletDto
     */
    public EventInfoByWalletDto getEventInfoByWallet(String walletAddress) {
        // 참여자 체크
        Participants participants = participantsRepository.findParticipantsByWalletAddress(walletAddress)
                .orElseThrow(() -> new ApiException(PARTICIPANT_NOT_FOUND));
        log.info("참여자 체크 완료");

        // 이벤트 체크
        Event event = eventRepository.findEventById(participants.getEvent().getId())
                .orElseThrow(() -> new ApiException(EVENT_NOT_FOUND));
        log.info("이벤트 체크 완료");

        eventRepository.updateCount(event.getId());

        // 이벤트 이미지 체크
        if (!isExists(event.getCid())) { throw new ApiException(IMAGE_NOT_FOUND);}
        log.info("이벤트 이미지 체크 완료");

        // 이벤트 이미지 가져오기
        BufferedImage eventImage = getEventImageFromIpfs(event.getCid());
        log.info("이벤트 이미지 가져오기 완료");

        // NFT 이미지 생성 (이벤트 이미지에 트위터 핸들 작성)
        BufferedImage nftImage = writeString(eventImage, participants.getTwitterHandle(), FONT, COLOR, X, Y);
        log.info("NFT 이미지 생성 완료");

        // IPFS에 NFT 이미지 저장
        String nfgImageCid = saveImageToIpfs(bufferedImageToByteArray(nftImage));
        log.info("NFT 이미지 저장 완료");

        // NFT 이미지를 담아 반환
        return EventInfoByWalletDto.builder()
                .walletAddress(participants.getWalletAddress())
                .twitterHandle(participants.getTwitterHandle())
                .count(event.getCount())
                .eventId(event.getId())
                .startDate(event.getStartDate().format(DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")))
                .endDate(event.getEndDate().format(DateTimeFormatter.ofPattern("yy.MM.dd hh:mm")))
                .eventTitle(event.getEventTitle())
                .address(event.getAddress())
                .nftImageCid(nfgImageCid)
                .build();
    }

    /**
     * 이벤트 상세보기: NFT 토큰 아이디를 받아 이벤트 정보를 넘겨줌
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

    private BufferedImage getEventImageFromIpfs(String cid){
        BufferedImage ipfsImage = null;
        RestTemplate rs = new RestTemplate();
        rs.getMessageConverters().add(new BufferedImageHttpMessageConverter());
        rs.getMessageConverters().add(new BufferedImageHttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "image/png");

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<BufferedImage> response = rs.exchange(
                URI.create(GATEWAY + cid),
                HttpMethod.GET,
                httpEntity,
                BufferedImage.class
        );

        ipfsImage = response.getBody();
        return ipfsImage;
    }

    private BufferedImage writeString(BufferedImage bufferedImage, String twitterHandle, Font font, Color color, int x, int y){
        Graphics graphics = bufferedImage.getGraphics();

        graphics.setFont(font);
        graphics.setColor(color);
        graphics.drawString(twitterHandle, x, y);
        graphics.dispose();

        return bufferedImage;
    }

    private byte[] bufferedImageToByteArray(BufferedImage input){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ImageIO.write(input, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("byteArray로 바꾸기 실패");
        }
        return baos.toByteArray();
    }

    private String saveImageToIpfs(byte[] nftImage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", KEY);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        RestTemplate rs = new RestTemplate();
        HttpEntity<byte[]> requestEntity = new HttpEntity<>(nftImage, headers);
        ResponseEntity<String> response = rs.exchange(
                URI.create(IPFS_API + "upload"),
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        HttpStatus status = response.getStatusCode();
        if (!status.is2xxSuccessful()) {
            throw new ApiException(IMAGE_SAVE_ERROR);}

        String cid = null;

        try {
            IpfsResponse ipfsResponse = objectMapper.readValue(response.getBody(), IpfsResponse.class);
            cid = ipfsResponse.getValue().getPin().getCid();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return cid;
    }

    /**
     * cid의 파일이 ipfs에 있는지 체크
     * @param cid 이벤트 이미지의 cid
     * @return boolean 존재 유무
     */
    private boolean isExists(String cid){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", KEY);

        RestTemplate rs = new RestTemplate();
        HttpEntity<?> responseEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = rs.exchange(
                URI.create(IPFS_API + cid),
                HttpMethod.GET,
                responseEntity,
                String.class
        );

        HttpStatus statusCode = response.getStatusCode();
        return statusCode.is2xxSuccessful();
    }
}
