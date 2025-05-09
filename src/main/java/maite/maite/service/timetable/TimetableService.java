package maite.maite.service.timetable;

import maite.maite.web.dto.timetable.request.TimetableRequestDto;
import maite.maite.web.dto.timetable.response.TimetableResponseDto;
import maite.maite.web.dto.timetable.response.UserTimetableResponseDto;

import java.util.List;

public interface TimetableService{
    TimetableResponseDto getTimetable(Long timetableId, Long userId);
    UserTimetableResponseDto getTimetableByEmail(String userEmail);
    TimetableResponseDto createTimetable(TimetableRequestDto request, Long userId);
    void deleteTimetable(Long timetableId, Long userId);

    List<TimetableResponseDto> getTimetablesByUserId(Long userId);
}
