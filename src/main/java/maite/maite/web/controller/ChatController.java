package maite.maite.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import maite.maite.service.ChatService;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "채팅 관련 API")
public class ChatController {

    private final ChatService chatService;

    //@Operation(summary = "채팅 보내기 API")
    //@PostMapping("/message")


}
