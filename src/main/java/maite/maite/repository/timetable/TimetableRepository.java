package maite.maite.repository.timetable;

import maite.maite.domain.entity.timetable.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    //List<Timetable> findByUserId(Long userId);
    @Query("SELECT t FROM Timetable t WHERE t.user.id = :userId")
    Optional<Timetable> findByUserId(@Param("userId") Long userId);
}
