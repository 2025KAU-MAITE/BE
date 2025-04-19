package maite.maite.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PendingRoomResponse {
    private Long id;
    private String email;
    private String name;
}
