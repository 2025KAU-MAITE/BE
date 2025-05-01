package maite.maite.web.dto.chat.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageUploadRequestDto {
    private Long senderId;
    private Long roomId;
    private MultipartFile image;
}
