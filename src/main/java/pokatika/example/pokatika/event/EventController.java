package pokatika.example.pokatika.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pokatika.example.pokatika.common.ApiResponse;
import pokatika.example.pokatika.event.dto.EventInfoByNftDto;
import pokatika.example.pokatika.event.dto.EventInfoByWalletDto;

import static pokatika.example.pokatika.common.StatusMessage.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("event")
public class EventController {

    private final EventService eventService;

    /**
     * 월렛 주소를 넘겨 받아 해당하는 지갑 주소, 이벤트 이미지, 순번을 넘겨줌
     * @param walletAddress 월랫 주소
     * @return eventInfoDto 지갑_주소, 순번, NFT이미지
     */
    @GetMapping("/by-wallet/{walletAddress}")
    public ResponseEntity<?> eventInfoByWallet (@PathVariable String walletAddress) {
        EventInfoByWalletDto eventInfoByWallet = eventService.getEventInfoByWallet(walletAddress);
        return ApiResponse.successWitchBody(SUCCESS, eventInfoByWallet);
    }

    /**
     * NFT 토큰 아이디를 받아 해당 하는 이벤트 정보를 넘겨줌
     * @param nftTokenId
     * @return
     */
    @GetMapping("/by-nft/{nftTokenId}")
    public ResponseEntity<?> eventInfoByNftToken (@PathVariable String nftTokenId) {
        EventInfoByNftDto eventInfoByNft = eventService.getEventInfoByNft(nftTokenId);
        return ApiResponse.successWitchBody(SUCCESS, eventInfoByNft);
    }
}
