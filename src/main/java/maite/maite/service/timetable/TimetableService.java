package maite.maite.service.timetable;

import lombok.RequiredArgsConstructor;
import maite.maite.web.dto.timetable.response.TimetableResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface TimetableService{
    TimetableResponseDto getTimetable(Long timetableId);
}
