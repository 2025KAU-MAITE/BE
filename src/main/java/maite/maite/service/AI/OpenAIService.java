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
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // =============================
    // 요약 메인 메서드
    // =============================
    public String summarize(String topic, String rawText) {
        int chunkSize = 1500; // 대략 한글 기준 1,500자 = 약 3,000~4,000 tokens 이하로 안전

        if (rawText.length() <= chunkSize) {
            return summarizeSingle(topic, rawText);
        }

        // 긴 입력: 나눠서 부분 요약 후 통합 요약
        List<String> chunks = splitText(rawText, chunkSize);
        StringBuilder intermediateSummaries = new StringBuilder();

        for (String chunk : chunks) {
            String partSummary = summarizeSingle(topic + " (부분)", chunk);
            intermediateSummaries.append(partSummary).append("\n");
        }

        // 통합 요약
        return summarizeSingle(topic + " (통합 요약)", intermediateSummaries.toString());
    }

    // =============================
    // 단일 요약 요청 처리
    // =============================
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
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 회의 내용을 간결하게 요약하는 전문가입니다."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions",
                    request,
                    Map.class
            );

            Map choices = ((List<Map>) response.getBody().get("choices")).get(0);
            Map message = (Map) choices.get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            return "요약 실패: " + e.getMessage();
        }
    }

    // =============================
    // 텍스트 분할 유틸리티
    // =============================
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

    public String answer(String rawText) {
        String prompt = String.format("""
                사용자에게서 들어온 질문에 대해 대답해주세요.
                
                문장이 어색하거나 이상하더라도 키워드를 통해 맥락을 생각해서 대답해주세요.
                또한 불필요하게 많은 정보를 제공하지 말고 질문에 맞는 적절한 대답만 간단히 해주세요.
                
                질문 내용:
                %s
                """, rawText);
        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", prompt)
                )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.openai.com/v1/chat/completions",
                    request,
                    Map.class
            );

            Map choices = ((List<Map>) response.getBody().get("choices")).get(0);
            Map message = (Map) choices.get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            return "대답 실패: " + e.getMessage();
        }
    }
}
