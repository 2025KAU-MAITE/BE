package maite.maite.repository.timetable;

import maite.maite.domain.entity.timetable.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByUserId(Long userId);
}
