package maite.maite.web.dto.User.SMS;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class SmsResponseDTO {
    private String code;
}
