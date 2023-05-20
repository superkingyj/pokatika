package pokatika.example.pokatika.nft.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveNftDto {
    private String walletAddress;
    private String nftId;
    private Long eventId;
}
