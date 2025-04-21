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
public class RoomResponse {
    private Long id;
    private String name;
    private String description;
    private String hostEmail;
    private List<String> participantEmails;
    private LocalDateTime createdAt;
}
