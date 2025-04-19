package maite.maite.web.controller;

import lombok.RequiredArgsConstructor;
import maite.maite.domain.entity.Room;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.RoomService;
import maite.maite.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<?> getMyRooms(@AuthenticationPrincipal CustomerUserDetails userDetails) {
        List<RoomResponse> rooms = roomService.getRoomsOfUser(userDetails.getUser());
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomDetail(@PathVariable Long roomId) {
        RoomResponse response = roomService.getRoomDetail(roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody RoomCreateRequest request,
                                        @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.createRoom(userDetails.getUser(), request);
        return ResponseEntity.ok("회의방이 생성되었습니다.");
    }

    @DeleteMapping("/{roomId}/me")
    public ResponseEntity<?> leaveRoom(@PathVariable Long roomId,
                                       @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.leaveRoom(roomId, userDetails.getUser());
        return ResponseEntity.ok("회의방에서 나갔습니다.");
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId,
                                        @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.deleteRoom(roomId, userDetails.getUser());
        return ResponseEntity.ok("회의방 삭제됨");
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId,
                                        @RequestBody RoomUpdateRequest request,
                                        @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.updateRoom(roomId, userDetails.getUser(), request);
        return ResponseEntity.ok("회의방 이름이 수정되었습니다.");
    }

    @PostMapping("/{roomId}/invites")
    public ResponseEntity<?> inviteUserToRoom(@PathVariable Long roomId,
                                              @RequestBody RoomInviteRequest request,
                                              @AuthenticationPrincipal CustomerUserDetails userDetails) {
        roomService.inviteUserToRoom(roomId, userDetails.getUser(), request.getEmail());
        return ResponseEntity.ok("사용자 초대 완료");
    }

    @GetMapping("/{roomId}/invites/pending")
    public ResponseEntity<?> getPendingInvitees(@PathVariable Long roomId) {
        List<PendingRoomResponse> pending = roomService.getPendingInvitees(roomId);
        return ResponseEntity.ok(pending);
    }
}

