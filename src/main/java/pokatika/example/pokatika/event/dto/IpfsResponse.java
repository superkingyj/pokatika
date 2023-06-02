package pokatika.example.pokatika.event.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpfsResponse {
    @JsonProperty("value")
    public Value value;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Value {

        @JsonProperty("pin")
        public Pin pin;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class Pin {
            @JsonProperty("cid")
            private String cid;
        }
    }
}
