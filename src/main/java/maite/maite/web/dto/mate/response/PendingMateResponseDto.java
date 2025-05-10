package maite.maite.web.dto.mate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingMateResponseDto {
    private Long requestId;
    private Long userId;
    private String name;
    private String email;
    private String profileImageUrl;
    private LocalDateTime createdAt;
}
