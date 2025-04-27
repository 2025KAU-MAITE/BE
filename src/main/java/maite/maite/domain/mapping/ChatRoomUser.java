package maite.maite.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import maite.maite.domain.Enum.ChatRoomUserRole;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.chat.ChatRoom;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // 마지막으로 읽은 메시지 ID (읽음 표시 기능용)
    private Long lastReadMessageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomUserRole role = ChatRoomUserRole.USER;

}
