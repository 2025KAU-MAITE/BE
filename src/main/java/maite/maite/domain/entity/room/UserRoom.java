package maite.maite.domain.entity.room;

import jakarta.persistence.*;
import lombok.*;
import maite.maite.domain.Enum.InviteStatus;
import maite.maite.domain.entity.User;

import java.time.LocalDateTime;

@Entity
@IdClass(UserRoomId.class)
@Table(name = "user_room")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoom {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InviteStatus status; // PENDING, ACCEPTED, REJECTED, EXITED

    @Column(nullable = false)
    private LocalDateTime invitedAt;

    @Column
    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        if (this.invitedAt == null) {
            this.invitedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = InviteStatus.PENDING;
        }
    }
}
