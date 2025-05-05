package maite.maite.web.dto.mate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponseDto {
    private Long id;
    private String name;
    private String email;
    private String profileImageUrl;
    private boolean isMate;
}
