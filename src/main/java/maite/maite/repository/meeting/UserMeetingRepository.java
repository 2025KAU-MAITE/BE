package maite.maite.repository.meeting;

import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.meeting.Meeting;
import maite.maite.domain.entity.meeting.UserMeeting;
import maite.maite.domain.entity.meeting.UserMeetingId;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMeetingRepository extends JpaRepository<UserMeeting, UserMeetingId>{
    List<UserMeeting> findAllByUserAndStatus(User user, InviteStatus status);
    boolean existsByMeetingAndUser(Meeting meeting, User user);
    boolean existsByMeetingAndUserAndStatus(Meeting meeting, User user, InviteStatus status);
    void deleteAllByMeeting(Meeting meeting);
    List<UserMeeting> findAllByMeetingAndStatus(Meeting meeting, InviteStatus inviteStatus);
    Optional<UserMeeting>  findByMeetingRoomIdAndUser(Long roomId, User user);
    Optional<UserMeeting> findByMeetingAndUser(Meeting meeting, User user);
    boolean existsByMeeting_RoomAndMeeting_Proposer(Room room, User user);
    List<UserMeeting> findAllByUserAndMeeting_Room(User user, Room room);
}
