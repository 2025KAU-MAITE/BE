package maite.maite.web.controller;

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
@RequiredArgsConstructor
public class ProfileController {

    private final AmazonS3Manager amazonS3Manager;
    private final S3Service s3Service;
    private final AmazonConfig amazonConfig;
    private final UserRepository userRepository;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
