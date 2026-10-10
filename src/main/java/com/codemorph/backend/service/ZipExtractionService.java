package com.codemorph.backend.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipExtractionService {

    private static final Set<String> IGNORED_DIRECTORIES = Set.of(
            "node_modules",
            ".git",
            ".gradle",
            "build",
            "dist",
            "target",
            ".idea",
            ".vscode",
            "out",
            "coverage",
            "__pycache__"
    );

    private static final Set<String> INCLUDED_FILES = Set.of(
            "pom.xml",
            "build.gradle",
            "build.gradle.kts",
            "settings.gradle",
            "settings.gradle.kts"
    );

    private static final long MAX_FILE_SIZE = 50L * 1024 * 1024;
    private static final long MAX_TOTAL_SIZE = 2L * 1024 * 1024 * 1024;
    private static final int MAX_ENTRIES = 100_000;

    public void extractRelevantFiles(
            InputStream inputStream,
            Path destination
    ) throws IOException {

        Path root = destination.toAbsolutePath().normalize();
        Files.createDirectories(root);

        long totalBytes = 0;
        int entryCount = 0;

        try (ZipInputStream zip = new ZipInputStream(inputStream)) {
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                entryCount++;

                if (entryCount > MAX_ENTRIES) {
                    throw new IOException("ZIP contains too many entries.");
                }

                String entryName = entry.getName().replace('\\', '/');

                if (entryName.startsWith("/") || entryName.matches("^[A-Za-z]:.*")) {
                    zip.closeEntry();
                    continue;
                }

                Path relativePath = Path.of(entryName).normalize();

                if (relativePath.isAbsolute()
                        || relativePath.startsWith("..")) {
                    zip.closeEntry();
                    continue;
                }

                Path output = root.resolve(relativePath).normalize();

                if (!output.startsWith(root)) {
                    zip.closeEntry();
                    continue;
                }

                if (shouldIgnore(entryName)) {
                    zip.closeEntry();
                    continue;
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(output);
                    zip.closeEntry();
                    continue;
                }

                if (!shouldInclude(relativePath)) {
                    zip.closeEntry();
                    continue;
                }

                Files.createDirectories(output.getParent());

                long fileBytes = 0;

                try (var out = Files.newOutputStream(output)) {
                    byte[] buffer = new byte[8192];
                    int read;

                    while ((read = zip.read(buffer)) != -1) {
                        fileBytes += read;
                        totalBytes += read;

                        if (fileBytes > MAX_FILE_SIZE) {
                            throw new IOException(
                                    "A project file exceeds the 50 MB limit: "
                                            + entryName
                            );
                        }

                        if (totalBytes > MAX_TOTAL_SIZE) {
                            throw new IOException(
                                    "Extracted project files exceed the 2 GB limit."
                            );
                        }

                        out.write(buffer, 0, read);
                    }
                }

                zip.closeEntry();
            }
        }
    }

    private boolean shouldIgnore(String entryName) {
        String[] parts = entryName.split("/");

        for (String part : parts) {
            if (IGNORED_DIRECTORIES.contains(part)) {
                return true;
            }
        }

        return false;
    }

    private boolean shouldInclude(Path relativePath) {
        String fileName = relativePath.getFileName()
                .toString()
                .toLowerCase();

        return fileName.endsWith(".java")
                || INCLUDED_FILES.contains(fileName);
    }
}
