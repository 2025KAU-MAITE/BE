package maite.maite.aws.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import maite.maite.config.AmazonConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;
    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();

        // 확장자 기반으로 Content-Type 설정 (문제 해결!)
        String contentType = determineContentType(file);
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        // 브라우저에서 바로 보기 설정
        if (contentType.startsWith("image/")) {
            metadata.setContentDisposition("inline");
        }

        try {
            PutObjectRequest putRequest = new PutObjectRequest(
                    amazonConfig.getBucket(),
                    keyName,
                    file.getInputStream(),
                    metadata
            );

            //putRequest.setCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putRequest);

        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    private String determineContentType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null) {
            String extension = originalFilename.toLowerCase();
            if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (extension.endsWith(".png")) {
                return "image/png";
            } else if (extension.endsWith(".gif")) {
                return "image/gif";
            } else if (extension.endsWith(".webp")) {
                return "image/webp";
            }
        }

        return "application/octet-stream";
    }

    public void deleteFile(String keyName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(amazonConfig.getBucket(), keyName));
        } catch (Exception e) {
            throw new RuntimeException("S3 파일 삭제 실패", e);
        }
    }

    public String generateProfile(Uuid uuid) {
        return amazonConfig.getProfilePath() + '/' + uuid.getUuid();
    }

    public String generateChat(Uuid uuid) {
        return amazonConfig.getChatPath() + '/' + uuid.getUuid();
    }
}