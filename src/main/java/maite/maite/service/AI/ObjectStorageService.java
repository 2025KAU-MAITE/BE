package maite.maite.service.AI;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ObjectStorageService {

    @Value("${object-storage.bucket}")
    private String bucket;

    @Value("${object-storage.region}")
    private String region;

    @Value("${object-storage.access-key}")
    private String accessKey;

    @Value("${object-storage.secret-key}")
    private String secretKey;

    private AmazonS3 s3Client;

    @PostConstruct
    public void initS3Client() {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);

        this.s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "https://kr.object.ncloudstorage.com", "kr-standard"
                        )
                )
                .withPathStyleAccessEnabled(true) // 💥 중요: 버킷 URL 방식으로 접근
                .withClientConfiguration(clientConfig)
                .withCredentials(new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    public String upload(MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            s3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Object Storage 업로드 실패", e);
        }
    }
}