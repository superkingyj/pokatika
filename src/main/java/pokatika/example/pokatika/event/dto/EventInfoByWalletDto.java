package pokatika.example.pokatika.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventInfoByWalletDto {
    private Long eventId;
    private String eventTitle;
    private String address;
    private String startDate;
    private String endDate;
    private String walletAddress;
    private String twitterHandle;
    private Long count;
    private String nftImageCid;
}
