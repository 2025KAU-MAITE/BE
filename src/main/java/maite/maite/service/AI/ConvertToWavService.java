package maite.maite.service.AI;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class ConvertToWavService {
    public File convertToWav(MultipartFile inputFile) throws IOException, InterruptedException {
        String ext = getExtension(inputFile.getOriginalFilename());

        File input = File.createTempFile("input-", "." + ext);
        inputFile.transferTo(input);

        File output = File.createTempFile("converted-", ".wav");

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-i", input.getAbsolutePath(),
                "-ar", "16000",
                "-ac", "1",
                output.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg 변환 실패");
        }

        return output;
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
