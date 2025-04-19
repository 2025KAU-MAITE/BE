package maite.maite.web.controller.timetable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.service.timetable.TimetableService;
import maite.maite.web.dto.timetable.request.TimetableRequestDto;
import maite.maite.web.dto.timetable.response.TimetableResponseDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
@Tag(name = "timetable", description = "시간표 관련 API")
public class TimetableController {

    private final TimetableService timetableService;

    // 시간표 조회 API
    @Operation(summary = "시간표 조회 API")
    @GetMapping("/{timetableId}")
    public ApiResponse<TimetableResponseDto> getTimetable(
            @PathVariable Long timetableId
    ) {
        return ApiResponse.onSuccess(timetableService.getTimetable(timetableId));
    }

    //시간표 생성 API
    @Operation(summary = "시간표 생성 API")
    @PostMapping
    public ApiResponse<TimetableResponseDto> createTimetable(
            @RequestBody TimetableRequestDto request
    ){
        return ApiResponse.onSuccess(timetableService.createTimetable(request));
    }

    //시간표 삭제 API
    @Operation(summary = "시간표 삭제 API")
    @DeleteMapping("/{timetableId}")
    public ApiResponse<Void> deleteTimetable(
            @PathVariable Long timetableId
    ){
        timetableService.deleteTimetable(timetableId);
        return ApiResponse.onSuccess(null);
    }

}
