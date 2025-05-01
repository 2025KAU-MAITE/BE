package maite.maite.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.*;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class SpeechToTextService {

    private SpeechClient createSpeechClient() throws Exception {
        String json = System.getenv("GOOGLE_CREDENTIALS");
        if (json == null || json.isBlank()) {
            throw new IllegalStateException("환경변수 GOOGLE_CREDENTIALS가 비어 있습니다.");
        }

        return SpeechClient.create(
                SpeechSettings.newBuilder()
                        .setCredentialsProvider(
                                FixedCredentialsProvider.create(
                                        ServiceAccountCredentials.fromStream(
                                                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))
                                        )
                                )
                        ).build()
        );
    }

    public String transcribe(MultipartFile file) throws Exception {
        try (SpeechClient speechClient = createSpeechClient()) {

            // 1. GCS에 업로드
            String gcsUri = uploadToGCS(file, "maite_audio_bucket");  // 버킷 이름 하드코딩 또는 주입

            // 2. GCS URI를 Audio로 설정
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setUri(gcsUri)
                    .build();

            // 3. 설정
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("ko-KR")
                    .build();

            // 4. 비동기 호출
            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speechClient.longRunningRecognizeAsync(config, audio);

            LongRunningRecognizeResponse result = response.get();  // 응답 대기

            StringBuilder transcriptBuilder = new StringBuilder();
            for (SpeechRecognitionResult res : result.getResultsList()) {
                transcriptBuilder.append(res.getAlternatives(0).getTranscript()).append("\n");
            }

            return transcriptBuilder.toString();
        }
    }



    public String uploadToGCS(MultipartFile file, String bucketName) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        String json = System.getenv("GOOGLE_CREDENTIALS");
        if (json == null || json.isBlank()) {
            throw new IllegalStateException("환경변수 GOOGLE_CREDENTIALS가 비어 있습니다.");
        }

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(
                        new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))))
                .build()
                .getService();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        storage.create(blobInfo, file.getBytes());
        return String.format("gs://%s/%s", bucketName, fileName);
    }
}