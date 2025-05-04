package maite.maite.service.timetable;

import maite.maite.web.dto.timetable.request.TimetableRequestDto;
import maite.maite.web.dto.timetable.response.TimetableResponseDto;

public interface TimetableService{
    TimetableResponseDto getTimetable(Long timetableId, Long userId);
    TimetableResponseDto createTimetable(TimetableRequestDto request, Long userId);
    void deleteTimetable(Long timetableId, Long userId);
}
