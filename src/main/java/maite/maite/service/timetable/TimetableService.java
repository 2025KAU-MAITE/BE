package maite.maite.service.timetable;

import maite.maite.web.dto.timetable.request.TimetableRequestDto;
import maite.maite.web.dto.timetable.response.TimetableResponseDto;

public interface TimetableService{
    TimetableResponseDto getTimetable(Long timetableId);
    TimetableResponseDto createTimetable(TimetableRequestDto request);
    void deleteTimetable(Long timetableId);
}
