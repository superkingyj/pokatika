package pokatika.example.pokatika.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pokatika.example.pokatika.common.ApiException;
import pokatika.example.pokatika.event.dto.EventInfoByNftDto;
import pokatika.example.pokatika.event.dto.EventInfo;
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
import java.io.InputStream;
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

    // 지금은 static한 값을 저장
    private static Font FONT;
    private static String FONT_NAME = "SK_Pupok_Solid_400.ttf";
    private static int X = 10;
    private static int Y = 50;
    private static float FONT_SIZE = 20f;
    private static Color COLOR = new Color(34, 79, 195);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EventService(ParticipantsRepository participantsRepository, EventRepository eventRepository, NftRepository nftRepository) {
        this.participantsRepository = participantsRepository;
        this.eventRepository = eventRepository;
        this.nftRepository = nftRepository;
        initFont();
    }

    private void initFont(){
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource("classpath:fonts/" + FONT_NAME);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            FONT = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            FONT = FONT.deriveFont(FONT_SIZE);
            inputStream.close();
        } catch (IOException | FontFormatException e) {
            log.error("폰트 읽어오기 실패");
            e.printStackTrace();
        }
    }

    /**
     * NFT 이미지 가져오기: 월렛 주소를 입력받아 해당하는 지갑 주소, 트위터 핸들이 박힌 이벤트 이미지, 순번을 넘겨줌
     * @param walletAddress 지갑 주소
     * @return EventInfoByWalletDto
     */
    @Transactional
    public EventInfo getEventInfoByWallet(String walletAddress) {
        // 참여자 체크
        Participants participants = participantsRepository.findParticipantsByWalletAddress(walletAddress)
                .orElseThrow(() -> new ApiException(PARTICIPANT_NOT_FOUND));

        // 이벤트 체크
        Event event = eventRepository.findEventById(participants.getEvent().getId())
                .orElseThrow(() -> new ApiException(EVENT_NOT_FOUND));

        eventRepository.updateCount(event.getId());

        if (!isExists(event.getCid())) { throw new ApiException(IMAGE_NOT_FOUND);}
        log.info("1. 이벤트 이미지 체크");

        BufferedImage eventImage = getEventImageFromIpfs(event.getCid());
        log.info("2. 이벤트 이미지 가져오기");

        BufferedImage nftImage = writeString(eventImage, participants.getTwitterHandle(), FONT, COLOR, X, Y);
        log.info("3. NFT 이미지 생성 (이벤트 이미지에 트위터 핸들 작성)");

        String nfgImageCid = saveImageToIpfs(bufferedImageToByteArray(nftImage));
        log.info("4. IPFS에 NFT 이미지 저장");

        Nft nft = Nft.builder()
                .event(event)
                .participants(participants)
                .cid(nfgImageCid)
                .build();

        participants.setNft(nft);

        nftRepository.save(nft);
        log.info("5. DB에 NFT cid 저장");

        return EventInfo.builder()
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

    @Transactional
    public EventInfo getEventInfoById(Long eventId, String walletAddress) {

        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new ApiException(EVENT_NOT_FOUND));

        if (!isExists(event.getCid())) { throw new ApiException(IMAGE_NOT_FOUND);}
        log.info("1. 이벤트 이미지 체크");

        BufferedImage eventImage = getEventImageFromIpfs(event.getCid());
        log.info("2. 이벤트 이미지 가져오기");

        BufferedImage nftImage = writeString(eventImage, extractName(walletAddress), FONT, COLOR, X, Y);
        log.info("3. NFT 이미지 생성 (이벤트 이미지에 월렛 주소 작성)");

        String nfgImageCid = saveImageToIpfs(bufferedImageToByteArray(nftImage));
        log.info("4. IPFS에 NFT 이미지 저장");

        Participants participants = Participants.builder()
                .walletAddress(walletAddress)
                .event(event)
                .isRsvp(true)
                .build();
        participantsRepository.save(participants);
        log.info("5. DB에 참여자 저장");

        Nft nft = Nft.builder()
                .event(event)
                .participants(participants)
                .cid(nfgImageCid)
                .build();

        nftRepository.save(nft);
        log.info("5. DB에 NFt cid 저장");

        participants.setNft(nft);

        // 6. NFT 이미지를 담아 반환
        return EventInfo.builder()
                .walletAddress(walletAddress)
                .twitterHandle(null)
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
     * 이벤트 상세보기: NFT 이미지 cid를 받아 이벤트 정보를 넘겨줌
     * @param nftCid NFT 이미지 cid
     * @return EventInfoByNftDto
     */
    public EventInfoByNftDto getEventInfoByNft(String nftCid){
        Nft nft = nftRepository.findByCid(nftCid)
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

    private String extractName(String walletAddress){
        int dotIndex = walletAddress.indexOf('.');
        return walletAddress.substring(0, dotIndex);
    }
}
