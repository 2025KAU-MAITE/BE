package maite.maite.service;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maite.maite.aws.S3.AmazonS3Manager;
import maite.maite.aws.S3.Uuid;
import maite.maite.aws.S3.UuidRepository;
import maite.maite.config.AmazonConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Manager amazonS3Manager;
    private final UuidRepository uuidRepository;
    private final AmazonConfig amazonConfig;

    /**
     * 채팅 이미지를 S3에 업로드
     *
     * @param file 업로드할 이미지 파일
     * @param roomId 채팅방 ID
     * @return 업로드된 이미지의 URL
     */
    public String uploadChatImage(MultipartFile file, Long roomId) {
        try {
            // 고유한 파일명 생성
            String uuid = generateUniqueUuid();

            // 파일 확장자 추출
            String extension = getFileExtension(file.getOriginalFilename());

            // S3 경로 구성: 채팅 경로/채팅방ID/UUID.확장자
            String keyName = String.format("%s/%d/%s%s",
                    amazonConfig.getChatPath(),
                    roomId,
                    uuid,
                    extension);

            // S3에 업로드
            String imageUrl = amazonS3Manager.uploadFile(keyName, file);

            //log.info("Image uploaded successfully: {}", imageUrl);
            return imageUrl;

        } catch (Exception e) {
            //log.error("Failed to upload image to S3: {}", e.getMessage());
            throw new RuntimeException("이미지 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 프로필 이미지를 S3에 업로드
     *
     * @param file 업로드할 프로필 이미지
     * @return 업로드된 이미지의 URL
     */
    public String uploadProfileImage(MultipartFile file) {
        try {
            // 고유한 파일명 생성
            String uuid = generateUniqueUuid();

            // 파일 확장자 추출
            String extension = getFileExtension(file.getOriginalFilename());

            // S3 경로 구성: 프로필 경로/사용자ID/UUID.확장자
            String keyName = String.format("%s/%s%s",
                    amazonConfig.getProfilePath(),
                    uuid,
                    extension);

            // S3에 업로드
            String imageUrl = amazonS3Manager.uploadFile(keyName, file);

            //log.info("Profile image uploaded successfully: {}", imageUrl);
            return imageUrl;

        } catch (Exception e) {
            //log.error("Failed to upload profile image to S3: {}", e.getMessage());
            throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 프로필 이미지를 변경
     *
     * @param file 새로 업로드할 프로필 이미지
     * @param currentImageUrl 기존에 사용 중인 프로필 이미지의 URL
     * @return 새로 업로드된 이미지의 URL
     */

    public String updateProfileImage(MultipartFile file, String currentImageUrl) {
        try {
            // 1. 기존 이미지 삭제
            if (currentImageUrl != null && !currentImageUrl.isEmpty() && !currentImageUrl.equals("https://maite-s3.s3.ap-northeast-2.amazonaws.com/profile/980edf16-2ec6-4505-893f-5e8d40f9d960.png")) {
                String oldKeyName = extractKeyFromUrl(currentImageUrl);
                amazonS3Manager.deleteFile(oldKeyName);
            }

            // 고유한 파일명 생성
            String uuid = generateUniqueUuid();

            // 파일 확장자 추출
            String extension = getFileExtension(file.getOriginalFilename());

            // S3 경로 구성: 프로필 경로/사용자ID/UUID.확장자
            String keyName = String.format("%s/%s%s",
                    amazonConfig.getProfilePath(),
                    uuid,
                    extension);

            // S3에 업로드
            String imageUrl = amazonS3Manager.uploadFile(keyName, file);

            return imageUrl;
        } catch (Exception e) {
            //log.error("Failed to upload profile image to S3: {}", e.getMessage());
            throw new RuntimeException("프로필 이미지 업로드에 실패했습니다.", e);
        }
    }

    /**
     * 고유한 UUID 생성 및 저장
     *
     * @return 생성된 UUID 문자열
     */
    private String generateUniqueUuid() {
        String uuid = UUID.randomUUID().toString();

        // UUID를 DB에 저장
        Uuid uuidEntity = Uuid.builder()
                .uuid(uuid)
                .build();

        uuidRepository.save(uuidEntity);

        return uuid;
    }

    /**
     * 파일명에서 확장자 추출
     *
     * @param filename 파일명
     * @return 추출된 확장자 (.포함)
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return ".jpg"; // 기본 확장자
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * S3 URL에서 Object Key 추출
     *
     * @param url S3 URL
     * @return Object Key
     */
    private String extractKeyFromUrl(String url) {
        String bucketUrl = String.format("https://%s.s3.%s.amazonaws.com/",
                amazonConfig.getBucket(),
                amazonConfig.getRegion());
        return url.replace(bucketUrl, "");
    }
}
