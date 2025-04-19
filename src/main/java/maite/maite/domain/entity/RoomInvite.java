package maite.maite.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import maite.maite.domain.Enum.InviteStatus;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInvite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id")
    private User inviter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id")
    private User invitee;

    @Enumerated(EnumType.STRING)
    private InviteStatus status; // PENDING, ACCEPTED, REJECTED

    private LocalDateTime invitedAt;

    private LocalDateTime respondedAt;

    @PrePersist
    protected void onCreate() {
        this.invitedAt = LocalDateTime.now();
        this.status = InviteStatus.PENDING;
    }
}
