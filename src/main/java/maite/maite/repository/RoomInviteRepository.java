package maite.maite.repository;

import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.Room;
import maite.maite.domain.entity.RoomInvite;
import maite.maite.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomInviteRepository extends JpaRepository<RoomInvite, Long>{
    boolean existsByRoomAndInviteeAndStatus(Room room, User invitee, InviteStatus status);
    List<RoomInvite> findAllByRoomAndStatus(Room room, InviteStatus status);
}
