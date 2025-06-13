package Layer.NewStudentManagement.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file, String branchCode) {
        try {
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = generateUniqueFileName(originalFileName);
            String key = branchCode + "/student-sys/docs"+"/" + uniqueFileName;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            return "https://" + bucketName + ".s3.amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        return Instant.now().getEpochSecond() + "_" + UUID.randomUUID() + extension;
    }

    public String copyStudentPhotoToAttendanceFaces(String studentPhotoUrl,String branchCode, String classRoomId, String rollNo) {
        try {
            String s3Prefix = "https://" + bucketName + ".s3.amazonaws.com/";
            if (!studentPhotoUrl.startsWith(s3Prefix)) {
                throw new IllegalArgumentException("Invalid S3 photo URL.");
            }

            String sourceKey = studentPhotoUrl.substring(s3Prefix.length());

            String extension = sourceKey.substring(sourceKey.lastIndexOf("."));

            String destKey = branchCode + "/student-sys/attendance_faces/" + classRoomId +"/"+ rollNo + extension;

            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(destKey)
                    .build();

            s3Client.copyObject(copyRequest);

            return s3Prefix + destKey;

        } catch (Exception e) {
            throw new RuntimeException("Failed to copy student photo to attendance_faces", e);
        }
    }

    public void deleteFileFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return; // Nothing to delete
        }

        try {
            String fileKey = fileUrl.startsWith("https://")
                    ? fileUrl.substring(fileUrl.indexOf(".com/") + 5)
                    : fileUrl;

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteRequest);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }


    public String uploadFileToExactPath(MultipartFile file, String key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            return "https://" + bucketName + ".s3.amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }



}
