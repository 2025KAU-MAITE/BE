package maite.maite.service.AI;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.storage.*;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class SpeechToTextService {

    @Autowired
    private CredentialsProvider credentialsProvider;

    @Autowired
    private ConvertToWavService convertToWavService;

    private SpeechClient createSpeechClient() throws Exception {
        return SpeechClient.create(
                SpeechSettings.newBuilder()
                        .setCredentialsProvider(credentialsProvider)
                        .build()
        );
    }

    public String transcribe(MultipartFile file) throws Exception {
        try (SpeechClient speechClient = createSpeechClient()) {

            // 0. WAV로 변환
            File wavFile = convertToWavService.convertToWav(file);
            // 1. GCS에 업로드
            String gcsUri = uploadToGCS(wavFile, "maite_audio_bucket");

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


    public String uploadToGCS(File file, String bucketName) throws IOException {
        String fileName = UUID.randomUUID() + "-" + file.getName();

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentialsProvider.getCredentials())  // 주입받은 CredentialsProvider 사용
                .build()
                .getService();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("audio/wav").build();

        storage.create(blobInfo, java.nio.file.Files.readAllBytes(file.toPath()));
        return String.format("gs://%s/%s", bucketName, fileName);
    }
}