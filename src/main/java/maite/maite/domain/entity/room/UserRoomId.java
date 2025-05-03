package maite.maite.domain.entity.room;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoomId implements Serializable {
    private Long room;
    private Long user;
}