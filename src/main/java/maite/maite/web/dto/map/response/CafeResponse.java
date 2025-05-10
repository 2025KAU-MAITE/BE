package maite.maite.web.dto.map.response;

import io.swagger.v3.oas.annotations.info.Info;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CafeResponse {
    private String title;
    private String category;
    private String roadAddress;
    private String description;
    private String mapx;
    private String mapy;
}