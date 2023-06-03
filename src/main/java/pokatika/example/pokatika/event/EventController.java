package pokatika.example.pokatika.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pokatika.example.pokatika.common.ApiResponse;
import pokatika.example.pokatika.event.dto.EventInfoByNftDto;
import pokatika.example.pokatika.event.dto.EventInfo;

import static pokatika.example.pokatika.common.StatusMessage.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/event")
@Slf4j
public class EventController {

    private final EventService eventService;

    /**
     * RSVP 민팅시 사용: 월렛 주소를 넘겨 받아 해당하는 지갑 주소, 이벤트 이미지, 순번을 넘겨줌
     * @param walletAddress 월랫 주소
     * @return eventInfoDto 지갑_주소, 순번, NFT이미지
     */
    @GetMapping("/by-wallet/{walletAddress}")
    public ResponseEntity<?> eventInfoByWallet (@PathVariable String walletAddress) {
        log.info("컨트롤러 진입");
        EventInfo eventInfo = eventService.getEventInfoByWallet(walletAddress);
        return ApiResponse.successWitchBody(SUCCESS, eventInfo);
    }

    /**
     * Trial 계정 민팅시 사용: 이벤트 아이디, 월렛 주소 넘겨 받아 월렛 주소, 이벤트 이미지, 순번을 넘겨줌
     * @param id
     * @return
     */
    @GetMapping("/by-id/{id}/{walletAddress}")
    public ResponseEntity<?> eventInfo(@PathVariable Long id, @PathVariable String walletAddress){
        log.info("컨트롤러 진입");
        EventInfo eventInfo = eventService.getEventInfoById(id, walletAddress);
        return ApiResponse.successWitchBody(SUCCESS, eventInfo);

    }
    /**
     * NFT 이미지의 cid를 받아 해당 하는 이벤트 정보를 넘겨줌
     * @param nftCid
     * @return
     */
    @GetMapping("/by-nft/{nftCid}")
    public ResponseEntity<?> eventInfoByNftToken (@PathVariable String nftCid) {
        EventInfoByNftDto eventInfoByNft = eventService.getEventInfoByNft(nftCid);
        return ApiResponse.successWitchBody(SUCCESS, eventInfoByNft);
    }
}
