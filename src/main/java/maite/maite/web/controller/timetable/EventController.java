package maite.maite.web.controller.timetable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.timetable.EventService;
import maite.maite.web.dto.timetable.request.EventRequestDto;
import maite.maite.web.dto.timetable.response.EventResponseDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/timetables/{timetableId}")
@RequiredArgsConstructor
@Tag(name = "event", description = "일정 관련 API")
public class EventController {

    private final EventService eventService;

    // 시간표 별 일정 조회 API
    @Operation(summary = "시간표 별 일정 조회 API")
    @GetMapping("/events")
    public ApiResponse<List<EventResponseDto>> getEventsByTimetable(
            @PathVariable Long timetableId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(eventService.getEventsByTimetable(timetableId, userId));
    }

    // 일정 조회 API
    @Operation(summary = "일정 조회 API")
    @GetMapping("/event/{eventId}")
    public ApiResponse<EventResponseDto> getEvent(
            @PathVariable Long timetableId,
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(eventService.getEvent(eventId, userId));
    }

    //일정 생성 API
    @Operation(summary = "일정 생성 API")
    @PostMapping("/event")
    public ApiResponse<EventResponseDto> createEvent(
            @PathVariable Long timetableId,
            @RequestBody EventRequestDto request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(eventService.createEvent(timetableId, request,userId));
    }

    //일정 수정 API
    @Operation(summary = "일정 수정 API")
    @PatchMapping("/event/{eventId}")
    public ApiResponse<EventResponseDto> updateEvent(
            @PathVariable Long timetableId,
            @PathVariable Long eventId,
            @RequestBody EventRequestDto request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        Long userId = userDetails.getUser().getId();
        return ApiResponse.onSuccess(eventService.updateEvent(eventId, request,userId));
    }

    //일정 삭제 API
    @Operation(summary = "일정 삭제 API")
    @DeleteMapping("/event/{eventId}")
    public ApiResponse<Void> deleteEvent(
            @PathVariable Long timetableId,
            @PathVariable Long eventId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        Long userId = userDetails.getUser().getId();
        eventService.deleteEvent(eventId,userId);
        return ApiResponse.onSuccess(null);
    }
}
