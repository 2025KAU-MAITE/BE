package maite.maite.web.controller.chat;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.domain.entity.chat.ChatRoom;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.chat.ChatService;
import maite.maite.web.dto.chat.request.ChatRoomGroupRequestDto;
import maite.maite.web.dto.chat.response.ChatRoomResponseDto;
import maite.maite.web.dto.chat.request.ChatRoomPersonalRequestDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats/rooms")
@RequiredArgsConstructor
@Tag(name = "ChatRoom", description = "채팅방 관련 API")
public class ChatRoomController {

    private final ChatService chatService;

    // 채팅방 생성 API
    @Operation(summary = "개인 채팅방 생성 API")
    @PostMapping
    public ApiResponse<ChatRoomResponseDto> createPersonalChatRoom(
            @RequestBody ChatRoomPersonalRequestDto request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        return ApiResponse.onSuccess(chatService.createPersonalChatRoom(userId, request.getReceiverId()));
    }

    //팀 채팅방 생성 API
    @Operation(summary = "팀 채팅방 생성 API")
    @PostMapping("/group")
    public ApiResponse<ChatRoomResponseDto> createGroupChatRoom(
            @RequestBody ChatRoomGroupRequestDto request,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ){
        Long userId = Long.parseLong(userDetails.getUsername());
        return ApiResponse.onSuccess(chatService.createGroupChatRoom(request.getRoomName(), userId, request.getMemberIds()));
    }

    //채팅방 목록 조회
    @Operation(summary = "채팅방 목록 조회 API")
    @GetMapping
    public ApiResponse<List<ChatRoomResponseDto>> getChatRooms(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return ApiResponse.onSuccess(chatService.getChatRoomsByUserId(userId));
    }

    //채팅방 상세 조회

    //채팅방 나가기? -> 매핑 테이블에서 자기 아이디만 삭제
    @Operation(summary = "채팅방 나가기 API")
    @DeleteMapping("/{roomId}/me")
    public ApiResponse<Void> leaveChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        chatService.leaveChatRoom(roomId, userId);
        return ApiResponse.onSuccess(null);
    }

    //채팅방 삭제
    @Operation(summary = "채팅방 삭제 API")
    @DeleteMapping("/{roomId}")
    public ApiResponse<Void> deleteChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        chatService.deleteChatRoom(roomId, userId);
        return ApiResponse.onSuccess(null);
    }

    //채팅 내역 조회
}
