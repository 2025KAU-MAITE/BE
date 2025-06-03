package maite.maite.web.controller.meeting;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.meeting.UserMeeting;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.meeting.MeetingService;
import maite.maite.web.dto.map.response.CafeResponse;
import maite.maite.web.dto.meeting.request.MeetingAddressRequest;
import maite.maite.web.dto.meeting.request.MeetingCreateRequest;
import maite.maite.web.dto.meeting.request.MeetingUpdateRequest;
import maite.maite.web.dto.meeting.response.MeetingCreateResponse;
import maite.maite.web.dto.meeting.response.MeetingResponse;
import maite.maite.web.dto.meeting.response.MeetingSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetings")
@Tag(name = "meeting", description = "회의 API")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "내가 속한 회의 목록 조회")
    @GetMapping
    public ResponseEntity<List<MeetingSummaryResponse>> getMyMeetings(
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        return ResponseEntity.ok(meetingService.getMeetingsOfUser(userDetails.getUser()));
    }

    @Operation(summary = "회의방 기준 회의 목록 조회")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<MeetingSummaryResponse>> getMeetingsByRoom(@PathVariable Long roomId,
                                                                          @AuthenticationPrincipal CustomerUserDetails userDetails) {
        return ResponseEntity.ok(meetingService.getMeetingsByRoom(roomId, userDetails.getUser()));
    }

    @Operation(summary = "회의 상세 조회")
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingResponse> getMeeting(@PathVariable Long meetingId) {
        return ResponseEntity.ok(meetingService.getMeetingDetail(meetingId));
    }

    @Operation(summary = "회의 생성")
    @PostMapping("/rooms/{roomId}")
    public ResponseEntity<MeetingCreateResponse> createMeeting(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomerUserDetails userDetails,
            @RequestBody MeetingCreateRequest request) {
        MeetingCreateResponse response = meetingService.createMeeting(roomId, userDetails.getUser(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회의 수정")
    @PutMapping("/{meetingId}")
    public ResponseEntity<Void> updateMeeting(@PathVariable Long meetingId,
                                              @AuthenticationPrincipal CustomerUserDetails userDetails,
                                              @RequestBody MeetingUpdateRequest request) {
        meetingService.updateMeeting(meetingId, userDetails.getUser(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회의 삭제")
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> deleteMeeting(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        meetingService.deleteMeeting(meetingId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회의 나가기")
    @DeleteMapping("/{meetingId}/me")
    public ResponseEntity<Void> leaveMeeting(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal CustomerUserDetails userDetails) {
        meetingService.leaveMeeting(meetingId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "원하는 주소 입력")
    @PostMapping("/{meetingId}/address")
    public ResponseEntity<Void> setMyMeetingAddress(@PathVariable Long meetingId,
                                                    @RequestBody MeetingAddressRequest address,
                                                    @AuthenticationPrincipal CustomerUserDetails userDetails) {
        meetingService.saveParticipantAddress(meetingId, userDetails.getUser(), address);
        return ResponseEntity.ok().build();
    }


    // ✅ 회의 주변 카페 검색
    @Operation(summary = "중심 위치 찾기")
    @GetMapping("/{meetingId}/cafes")
    public ResponseEntity<List<CafeResponse>> getNearbyCafes(@PathVariable Long meetingId) {
        List<CafeResponse> cafes = meetingService.findMeetingNearbyCafes(meetingId);
        return ResponseEntity.ok(cafes);
    }

    @Operation(summary = "회의 장소 입력")
    @PatchMapping("/{meetingId}/select-place")
    public ResponseEntity<Void> selectMeetingPlace(
            @PathVariable Long meetingId,
            @RequestBody MeetingAddressRequest request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        meetingService.setMeetingPlaceName(meetingId, userDetails.getUser(), request.getAddress());
        return ResponseEntity.ok().build();
    }
}