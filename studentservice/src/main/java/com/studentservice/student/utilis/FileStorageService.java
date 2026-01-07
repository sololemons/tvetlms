package com.studentservice.student.utilis;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String storeFile(MultipartFile file) {
        try {
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = root.resolve(filename);

            Files.copy(file.getInputStream(), destination);

            return destination.toString();

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}


