package maite.maite.web.controller;

import lombok.RequiredArgsConstructor;
import maite.maite.service.OpenAIService;
import maite.maite.service.SpeechToTextService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SpeechSummaryController {

    private final SpeechToTextService speechToTextService;
    private final OpenAIService openAIService;

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
}
