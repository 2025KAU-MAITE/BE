package maite.maite.domain.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maite.maite.domain.mapping.ChatRoomUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String roomName;

    private LocalDateTime createdAt;

    // 상대방 닉네임을 받아서 ChatRoom 생성
    /*public static ChatRoom createWithOpponent(String opponentNickname) {
        return ChatRoom.builder()
                .roomName(opponentNickname)
                .createdAt(LocalDateTime.now())
                .build();
    }*/

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();
}
