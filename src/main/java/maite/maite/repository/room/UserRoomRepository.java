package maite.maite.repository.room;

import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.room.Room;
import maite.maite.domain.entity.room.UserRoom;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.room.UserRoomId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, UserRoomId>{
    List<UserRoom> findAllByUserAndStatus(User user, InviteStatus status);
    List<UserRoom> findAllByRoomAndStatus(Room room, InviteStatus status);
    boolean existsByRoomAndUser(Room room, User user);
    boolean existsByRoomAndUserAndStatus(Room room, User user, InviteStatus status);
    Optional<UserRoom> findByRoomAndUser(Room room, User user);
    void deleteAllByRoom(Room room);
}
