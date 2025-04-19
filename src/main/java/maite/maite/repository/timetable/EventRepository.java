package maite.maite.repository.timetable;

import maite.maite.domain.entity.timetable.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByTimetableId(Long timetableId);
}
