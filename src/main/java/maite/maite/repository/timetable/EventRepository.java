package maite.maite.repository.timetable;

import maite.maite.domain.entity.timetable.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByTimetableId(Long timetableId);
    List<Event> findByTimetableId(Long timetableId);  // 이 메소드 추가
    List<Event> findByTimetableIdAndDay(Long timetableId, String day);
}
