package maite.maite.repository.meeting;

import maite.maite.domain.entity.meeting.Meeting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByRoom_Id(Long roomId);
    Optional<Meeting> findById(Long meetingId);
}