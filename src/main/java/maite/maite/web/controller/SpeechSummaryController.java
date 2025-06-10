package maite.maite.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.domain.entity.User;
import maite.maite.domain.entity.meeting.Meeting;
import maite.maite.repository.SubscriptionRepository;
import maite.maite.repository.meeting.MeetingRepository;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.AI.*;
import maite.maite.web.dto.AI.ClovaResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/api/AI")
@Tag(name = "AI", description = "AI 호출, 요약 관련 API")
@RequiredArgsConstructor
public class SpeechSummaryController {

    private final SpeechToTextService speechToTextService;
    private final GeminiService geminiService;
    private final OpenAIService openAIService;
    private final TextToSpeechService textToSpeechService;
    private final ClovaSpeechService clovaSpeechService;
    private final SubscriptionRepository subscriptionRepository;
    private final MeetingRepository meetingRepository;

    @PostMapping(value = "/summary", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> summarizeFromSpeech(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topic") String topic,
            @RequestParam("meeting") Long meetingId
    ) {
        try {
            Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("해당 meeting이 존재하지 않습니다."));
            // 1. STT
            String transcript = clovaSpeechService.uploadAndConvert(file);
            // 2. 요약
            String summary = openAIService.summarize(topic, transcript);
//            String summary = geminiService.summarize(topic, transcript);

            meeting.setTextSum(summary);
            meetingRepository.save(meeting);
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
            String transcript = clovaSpeechService.uploadAndConvert(file);
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
            @RequestParam("topic") String topic,
            @RequestParam("meeting") Long meetingId,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        boolean isPaid = subscriptionRepository.existsByUserId(user.getId());
        if (!isPaid) {
            return ApiResponse.onFailure("SUBSCRIPTION_REQUIRED", "유료 결제가 필요한 서비스입니다.", null);
        }

        String transcript = clovaSpeechService.uploadAndConvert(file);

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("해당 meeting이 존재하지 않습니다."));
        meeting.setRecordText(transcript);

        String summary = openAIService.summarize(topic, transcript);
        meeting.setTextSum(summary);
        meetingRepository.save(meeting);

        ClovaResult clovaResult = ClovaResult.builder()
                .transcript(transcript)
                .result(summary)
                .build();
        return ApiResponse.onSuccess(clovaResult);
    }
}
