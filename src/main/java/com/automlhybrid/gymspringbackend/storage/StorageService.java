package com.automlhybrid.gymspringbackend.storage;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StorageService {

    // Folder where Python can also read files (Update this path!)
    // If Python is running locally, use a shared folder.
    private final String UPLOAD_DIR = "C:/AutoML_Hybrid/uploads/";

    public String uploadFile(MultipartFile file) throws IOException {
        // 1. Create directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Generate a unique name (to prevent overwriting)
        String originalName = file.getOriginalFilename();
        String fileKey = UUID.randomUUID() + "_" + originalName;

        // 3. Save to Disk
        Path filePath = uploadPath.resolve(fileKey);
        file.transferTo(filePath.toFile());

        System.out.println("✅ File saved locally at: " + filePath.toString());

        // Return the identifier that Python will use to find the file
        return fileKey;
    }
}
