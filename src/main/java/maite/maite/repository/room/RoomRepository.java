package maite.maite.repository.room;

import maite.maite.domain.entity.room.Room;
import maite.maite.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    //List<Room> findAllByParticipantsContaining(User user);
    // Optional<Room> findByRoomName(String name);
}