package maite.maite.web.dto.AI;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClovaResult {
    String transcript;
    String result;
}
