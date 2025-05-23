package maite.maite.service.AI;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClovaSpeechService {

    private final ObjectStorageService objectStorageService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${clova.speech.secret-key}")
    private String secretKey;

    @Value("${clova.speech.invoke-url}")
    private String invokeUrl;

    public String requestSpeechToText(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-CLOVASPEECH-API-KEY", secretKey);

        // diarization 설정 추가
        Map<String, Object> diarization = new HashMap<>();
        diarization.put("enable", true);
        diarization.put("speakerCountMin", -1);
        diarization.put("speakerCountMax", -1);

        Map<String, Object> body = new HashMap<>();
        body.put("dataKey", fileName);
        body.put("language", "ko-KR");
        body.put("completion", "sync");
        body.put("callback", "");
        body.put("fullText", true);
        body.put("diarization", diarization);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                invokeUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return formatSpeakerSegments(root);
        } catch (Exception e) {
            throw new RuntimeException("STT 결과 파싱 실패", e);
        }
    }

    public String uploadAndConvert(MultipartFile file) {
        String fileName = objectStorageService.upload(file); // "hello.mp3" 형태
        return requestSpeechToText(fileName);
    }

    private String formatSpeakerSegments(JsonNode root) {
        StringBuilder result = new StringBuilder();

        JsonNode segments = root.path("segments");
        JsonNode speakers = root.path("speakers");

        Map<String, String> speakerMap = new HashMap<>();
        for (JsonNode speaker : speakers) {
            String label = speaker.path("label").asText();
            String name = speaker.path("name").asText();
            speakerMap.put(label, name);
        }

        for (JsonNode segment : segments) {
            String label = segment.path("diarization").path("label").asText();
            String speakerName = speakerMap.getOrDefault(label, "Unknown");
            String text = segment.path("text").asText();

            result.append("[").append(speakerName).append("] ").append(text).append("\n");
        }

        return result.toString();
    }
}