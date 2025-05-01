package maite.maite.web.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import maite.maite.domain.Enum.Gender;

@Builder
@Getter
@AllArgsConstructor
public class InfoResponse {
    private String name;
    private String email;
    private String phonenumber;
}
