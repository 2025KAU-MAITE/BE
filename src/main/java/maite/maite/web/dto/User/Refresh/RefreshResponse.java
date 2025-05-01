package maite.maite.web.dto.User.Refresh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RefreshResponse {
    private String accessToken;
}
