package maite.maite.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.RoomService;
import maite.maite.web.dto.room.request.RoomCreateRequest;
import maite.maite.web.dto.room.request.RoomInviteRequest;
import maite.maite.web.dto.room.request.RoomUpdateRequest;
import maite.maite.web.dto.room.response.PendingRoomResponse;
import maite.maite.web.dto.room.response.RoomResponse;
import maite.maite.web.dto.room.response.RoomSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@Tag(name = "room", description = "회의방 API")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "내가 속한 회의방 목록 조회 API")
    @GetMapping
    public ResponseEntity<?> getMyRooms(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        List<RoomSummaryResponse> rooms = roomService.getRoomsOfUser(userDetails.getUser());
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "회의방 상세 조회 API")
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomDetail(@PathVariable Long roomId) {
        RoomResponse response = roomService.getRoomDetail(roomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회의방 생성 API")
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateRequest request,
                                        @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.createRoom(userDetails.getUser(), request);
        return ResponseEntity.ok("회의방이 생성되었습니다.");
    }

    @Operation(summary = "회의방 나가기 API")
    @DeleteMapping("/{roomId}/me")
    public ResponseEntity<?> leaveRoom(@PathVariable Long roomId,
                                       @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.leaveRoom(roomId, userDetails.getUser());
        return ResponseEntity.ok("회의방에서 나갔습니다.");
    }

    @Operation(summary = "회의방 삭제 API")
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId,
                                        @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.deleteRoom(roomId, userDetails.getUser());
        return ResponseEntity.ok("회의방 삭제됨");
    }

    @Operation(summary = "회의방 수정 API")
    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId,
                                        @RequestBody RoomUpdateRequest request,
                                        @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.updateRoom(roomId, userDetails.getUser(), request);
        return ResponseEntity.ok("회의방 이름이 수정되었습니다.");
    }

    @Operation(summary = "초대 API")
    @PostMapping("/{roomId}/invites")
    public ResponseEntity<?> inviteUserToRoom(@PathVariable Long roomId,
                                              @RequestBody RoomInviteRequest request,
                                              @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.inviteUserToRoom(roomId, userDetails.getUser(), request.getEmail());
        return ResponseEntity.ok("사용자 초대 완료");
    }

    @Operation(summary = "초대(보류)중인 사람 목록 조회 API")
    @GetMapping("/{roomId}/invites/pending")
    public ResponseEntity<?> getPendingInvitees(@PathVariable Long roomId) {
        List<PendingRoomResponse> pending = roomService.getPendingInvitees(roomId);
        return ResponseEntity.ok(pending);
    }
}

