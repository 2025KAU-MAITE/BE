package maite.maite.service.AI;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AudioChunkerService {
    // 오디오를 'chunkSeconds' 단위로 쪼갬 (예: 30초)
    public List<File> splitAudioByDuration(File wavFile, int chunkSeconds) throws Exception {
        String outputDir = wavFile.getParent();
        String chunkPattern = outputDir + File.separator + "chunk_%03d.wav";
        String[] cmd = {
                "ffmpeg",
                "-i", wavFile.getAbsolutePath(),
                "-f", "segment",
                "-segment_time", String.valueOf(chunkSeconds),
                "-c", "copy",
                chunkPattern
        };
        Process p = new ProcessBuilder(cmd).inheritIO().start();
        int exitCode = p.waitFor();
        if (exitCode != 0) throw new RuntimeException("ffmpeg split failed");

        File[] chunkFiles = new File(outputDir).listFiles(
                (FilenameFilter) (dir, name) -> name.startsWith("chunk_") && name.endsWith(".wav")
        );
        List<File> result = new ArrayList<>();
        if (chunkFiles != null) for (File f : chunkFiles) result.add(f);
        return result;
    }
}