package maite.maite.web.dto.room.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSummaryResponse {
    private Long roomId;
    private String name;
    private String hostName;
    private String hostEmail;
    private String description;
}