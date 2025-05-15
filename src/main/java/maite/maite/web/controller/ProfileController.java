package maite.maite.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maite.maite.apiPayload.ApiResponse;
import maite.maite.aws.S3.AmazonS3Manager;
import maite.maite.config.AmazonConfig;
import maite.maite.domain.entity.User;
import maite.maite.repository.UserRepository;
import maite.maite.security.CustomerUserDetails;
import maite.maite.service.S3Service;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "ProfileImage", description = "프로필이미지 변경 관련 API")
@RequiredArgsConstructor
public class ProfileController {

    private final AmazonS3Manager amazonS3Manager;
    private final S3Service s3Service;
    private final AmazonConfig amazonConfig;
    private final UserRepository userRepository;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로필 이미지 변경 API", description = "프로필이미지 변경")
    public ApiResponse<String> updateProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        String currentImageUrl = user.getProfileImageUrl();
        String newImageUrl = s3Service.updateProfileImage(file, currentImageUrl);
        user.setProfileImageUrl(newImageUrl);
        userRepository.save(user);

        return ApiResponse.onSuccess(newImageUrl);
    }

    @PostMapping("/image-to-basic")
    @Operation(summary = "기본 이미지 변경 API", description = "프로필이미지 변경(기본이미지) 요청")
    public ApiResponse<String> updateProfileImageToBasic(
            @AuthenticationPrincipal CustomerUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        String currentImageUrl = user.getProfileImageUrl();
        String newImageUrl = "https://maite-s3.s3.ap-northeast-2.amazonaws.com/profile/980edf16-2ec6-4505-893f-5e8d40f9d960.png";

        String bucketUrl = String.format("https://%s.s3.%s.amazonaws.com/",
                amazonConfig.getBucket(),
                amazonConfig.getRegion());
        String key = currentImageUrl.replace(bucketUrl, "");

        amazonS3Manager.deleteFile(key);
        user.setProfileImageUrl(newImageUrl);
        userRepository.save(user);

        return ApiResponse.onSuccess(newImageUrl);
    }
}
