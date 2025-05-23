package maite.maite.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.service.AI.ClovaSpeechService;
import maite.maite.service.AI.TextToSpeechService;
import maite.maite.service.AI.OpenAIService;
import maite.maite.service.AI.SpeechToTextService;
import maite.maite.web.dto.AI.ClovaResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/api/AI")
@Tag(name = "AI", description = "AI 호출, 요약 관련 API")
@RequiredArgsConstructor
public class SpeechSummaryController {

    private final SpeechToTextService speechToTextService;
    private final OpenAIService openAIService;
    private final TextToSpeechService textToSpeechService;
    private final ClovaSpeechService clovaSpeechService;

    @PostMapping(value = "/summary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> summarizeFromSpeech(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topic") String topic
    ) {
        try {
            // 1. STT
            String transcript = speechToTextService.transcribe(file);

            // 2. 요약
            String summary = openAIService.summarize(topic, transcript);

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("요약 실패: " + e.getMessage());
        }
    }

    @PostMapping(value = "/reply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> answerFromQuestion(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // 1. STT
            String transcript = speechToTextService.transcribe(file);
            // 2. 요약
            String summary = openAIService.answer(transcript);
            // 3. TTS
            byte[] audioData = textToSpeechService.synthesizeSpeech(summary);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("audio/mpeg"));
            return new ResponseEntity<>(audioData, headers, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("대답 실패: " + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        }
    }

    @PostMapping(value = "/summary-clova", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ClovaResult> convertSpeechToText(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topic") String topic
    ) {
        String transcript = clovaSpeechService.uploadAndConvert(file);
        String result = openAIService.summarize(topic, transcript);
        ClovaResult clovaResult = ClovaResult.builder()
                .transcript(transcript)
                .result(result)
                .build();
        return ApiResponse.onSuccess(clovaResult);
    }
}
