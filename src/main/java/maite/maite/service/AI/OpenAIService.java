package maite.maite.service.AI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String summarize(String topic, String rawText) {

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
