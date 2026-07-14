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
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    private static final String SYSTEM_NAME = "student-webclient-sys";
    private static final String FOLDER_NAME = "doct";

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
            return; // No file to delete
        }

        try {
            // Parse the URL and extract the path, removing the leading slash
            URI uri = new URI(fileUrl);
            String fileKey = uri.getPath().startsWith("/")
                    ? uri.getPath().substring(1)
                    : uri.getPath();

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteRequest);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3: " + fileUrl, e);
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


    // ✅ Upload a single image to 'doct/' folder with UUID file name
    public String uploadImage(MultipartFile file, String branchCode) throws IOException {
        if (branchCode == null || branchCode.isBlank()) {
            throw new IllegalArgumentException("Branch code is required");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is missing or empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("File must have a valid name");
        }

        // Use original file name
        String fileKey = branchCode + "/webclient-sys/doct/" + originalFilename;

        // Save file temporarily
        java.nio.file.Path tempPath = Files.createTempFile("upload-", originalFilename);
        file.transferTo(tempPath.toFile());

        // Upload to S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempPath));

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileKey;
    }



    // ✅ Delete image from S3 (from doct folder)
    public void deleteImage(String fileUrl) {
        try {
            // Extract S3 key from URL
            String key = fileUrl.substring(fileUrl.indexOf(SYSTEM_NAME + "/"));

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("Successfully deleted image from S3: " + key);

        } catch (Exception e) {
            System.err.println("Failed to delete image from S3: " + e.getMessage());
            throw new RuntimeException("Failed to delete image from S3", e);
        }
    }

}
