package maite.maite.web.dto.map.response;

import java.util.List;
import lombok.Data;

@Data
public class GeocodeResponse {
    private List<Address> addresses;

    @Data
    public static class Address {
        private String roadAddress;
        private String jibunAddress;
        private String x; // 경도
        private String y; // 위도
    }
}
