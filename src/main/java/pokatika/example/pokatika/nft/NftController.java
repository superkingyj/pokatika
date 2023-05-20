package pokatika.example.pokatika.nft;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pokatika.example.pokatika.common.ApiResponse;
import pokatika.example.pokatika.nft.dto.SaveNftDto;

import static pokatika.example.pokatika.common.StatusMessage.SUCCESS;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NftController {

    private final NftService nftService;

    /**
     * 월렛 주소, NFT 토큰 아이디를 받아 저장
     * @param saveNftDto 월렛 주소, NFT 토큰 아이디
     * @return ResponseEntity
     */
    @PostMapping("/nft")
    public ResponseEntity<?> saveNft(@RequestBody SaveNftDto saveNftDto){
        log.info("walletAddress = {}", saveNftDto.getWalletAddress());
        log.info("NftId = {}", saveNftDto.getNftId());
        log.info("eventId = {}", saveNftDto.getEventId());
        nftService.save(saveNftDto.getWalletAddress(), saveNftDto.getNftId(), saveNftDto.getEventId());
        return ApiResponse.successWithNothing(SUCCESS);
    }

}
