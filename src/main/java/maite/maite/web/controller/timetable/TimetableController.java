package maite.maite.web.controller.timetable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.timetable.TimetableService;
import maite.maite.web.dto.timetable.request.TimetableRequestDto;
import maite.maite.web.dto.timetable.response.TimetableResponseDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetables")
@RequiredArgsConstructor
@Tag(name = "timetable", description = "시간표 관련 API")
public class TimetableController {

    private final TimetableService timetableService;

    // 자기 시간표 조회 API
    @Operation(summary = "자기 시간표 조회 API")
    @GetMapping("/my")
    public ApiResponse<List<TimetableResponseDto>> getMyTimetables(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(timetableService.getTimetablesByUserId(userId));
    }

    // 시간표 조회 API
    @Operation(summary = "타 유저 시간표 조회 API")
    @GetMapping("/{timetableId}")
    public ApiResponse<TimetableResponseDto> getTimetable(
            @PathVariable Long timetableId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(timetableService.getTimetable(timetableId, userId));
    }

    //시간표 생성 API
    @Operation(summary = "시간표 생성 API")
    @PostMapping
    public ApiResponse<TimetableResponseDto> createTimetable(
            @RequestBody TimetableRequestDto request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(timetableService.createTimetable(request, userId));
    }

    //시간표 삭제 API
    @Operation(summary = "시간표 삭제 API")
    @DeleteMapping("/{timetableId}")
    public ApiResponse<Void> deleteTimetable(
            @PathVariable Long timetableId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        Long userId = userDetails.getUser().getId();
        timetableService.deleteTimetable(timetableId, userId);
        return ApiResponse.onSuccess(null);
    }

}
