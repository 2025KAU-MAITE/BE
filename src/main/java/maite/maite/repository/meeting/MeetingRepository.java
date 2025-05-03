package maite.maite.repository.meeting;

import maite.maite.domain.entity.User;
import maite.maite.domain.entity.meeting.Meeting;
import maite.maite.domain.entity.meeting.UserMeeting;
import maite.maite.domain.entity.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByRoom_Id(Long roomId);
}