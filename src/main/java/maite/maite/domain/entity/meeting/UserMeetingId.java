package maite.maite.domain.entity.meeting;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMeetingId implements Serializable {
    private Long meeting;
    private Long user;
}