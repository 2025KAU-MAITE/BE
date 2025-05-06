package maite.maite.service.AI;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.texttospeech.v1.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TextToSpeechService {

    @Autowired
    private CredentialsProvider credentialsProvider;

    public byte[] synthesizeSpeech(String text) throws Exception {
        try (TextToSpeechClient ttsClient = TextToSpeechClient.create(
                TextToSpeechSettings.newBuilder()
                        .setCredentialsProvider(credentialsProvider)
                        .build()
        )) {
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("ko-KR")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            SynthesizeSpeechResponse response = ttsClient.synthesizeSpeech(input, voice, audioConfig);
            return response.getAudioContent().toByteArray();  // byte[] 형태로 음성 반환
        }
    }
}