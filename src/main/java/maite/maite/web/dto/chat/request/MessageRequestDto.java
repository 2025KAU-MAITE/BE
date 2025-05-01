package maite.maite.web.dto.chat.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDto {
    private Long senderId;
    private String content; //text?
}
