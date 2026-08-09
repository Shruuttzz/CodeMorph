package com.codemorph.backend.service;

import com.codemorph.backend.model.ProjectSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class UploadService {

    public ProjectSummary processZip(MultipartFile file) throws IOException {

        // Create a temporary directory
        Path tempDir = Files.createTempDirectory("uploadedProject");

        // Extract ZIP
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {

            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                Path filePath = tempDir.resolve(entry.getName());

                if (entry.isDirectory()) {

                    Files.createDirectories(filePath);

                } else {

                    Files.createDirectories(filePath.getParent());

                    Files.copy(zis, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                zis.closeEntry();
            }
        }

        // Count Java files
        int javaFiles = (int) Files.walk(tempDir)
                .filter(path -> path.toString().endsWith(".java"))
                .count();

        String projectName = file.getOriginalFilename();

        if (projectName != null && projectName.endsWith(".zip")) {
            projectName = projectName.substring(0, projectName.length() - 4);
        }

        return new ProjectSummary(projectName, javaFiles);
    }
}