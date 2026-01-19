package com.automlhybrid.gymspringbackend.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class StorageService {

    private final MinioClient minioClient;
    private final String rawBucket;

    // Inject values from application.properties
    public StorageService(
            @Value("${app.minio.url:http://minio:9000}") String url,
            @Value("${app.minio.access-key:minioadmin}") String accessKey,
            @Value("${app.minio.secret-key:minioadmin}") String secretKey,
            @Value("${app.minio.buckets.raw:raw-data}") String rawBucket) {

        this.rawBucket = rawBucket;
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadFile(MultipartFile file) throws Exception {
        String originalName = file.getOriginalFilename();
        // Create a unique key: "user-uuid/filename.csv"
        // (Using a simple UUID prefix for now to keep it flat but unique)
        String fileKey = UUID.randomUUID() + "-" + originalName;

        // 1. Ensure Bucket Exists
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(rawBucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(rawBucket).build());
            System.out.println("✅ Bucket created: " + rawBucket);
        }

        // 2. Stream to MinIO
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(rawBucket)
                            .object(fileKey)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }

        System.out.println("✅ File uploaded to MinIO: " + fileKey);

        // Return the key that Python will use to fetch the file
        return fileKey;
    }
}