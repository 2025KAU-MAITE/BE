package maite.maite.web.dto.room.request;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomCreateRequest {
    private String name;
    private String description;
    private List<String> inviteEmails;
}