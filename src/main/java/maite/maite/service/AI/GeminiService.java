package maite.maite.service.AI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String summarize(String topic, String rawText) {
        System.out.println("Using Gemini Service");
        int chunkSize = 1500;

        if (rawText.length() <= chunkSize) {
            return summarizeSingle(topic, rawText);
        }

        List<String> chunks = splitText(rawText, chunkSize);
        StringBuilder intermediateSummaries = new StringBuilder();

        for (String chunk : chunks) {
            String partSummary = summarizeSingle(topic + " (부분)", chunk);
            intermediateSummaries.append(partSummary).append("\n");
        }

        return summarizeSingle(topic + " (통합 요약)", intermediateSummaries.toString());
    }

    private String summarizeSingle(String topic, String rawText) {
        String prompt = String.format("""
            회의 주제: %s

            아래 회의 내용을 요약해 주세요.
            문장이 어색하거나 반복되더라도 의미를 정리해서 핵심 위주로 요약해 주세요.
            만약 회의 내용 중에 다음 회의에 대한 약속이 존재한다면 따로 마지막 줄에 정리해 주세요.

            회의 내용:
            %s
            """, topic, rawText);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url,
                    request,
                    Map.class
            );

            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "요약 실패: Gemini 응답이 비어있습니다.";
            }
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            return "요약 실패: " + e.getMessage();
        }
    }

    private List<String> splitText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }
}