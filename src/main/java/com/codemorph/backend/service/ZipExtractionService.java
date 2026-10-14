
package com.codemorph.backend.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Locale;
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

        // Prevent duplicate ZIP entries from overwriting extracted files.
        Set<Path> extractedPaths = new HashSet<>();

        try (ZipInputStream zip = new ZipInputStream(inputStream)) {

            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {

                try {
                    entryCount++;

                    if (entryCount > MAX_ENTRIES) {
                        throw new IOException(
                                "ZIP contains too many entries."
                        );
                    }

                    String entryName = entry.getName()
                            .replace('\\', '/');

                    /*
                     * STEP 1: Reject absolute paths and Windows drive paths.
                     */
                    if (entryName.startsWith("/")
                            || entryName.matches("^[A-Za-z]:.*")) {
                        throw new IOException(
                                "Invalid absolute path in ZIP: " + entryName
                        );
                    }

                    /*
                     * STEP 2: Normalize and validate the relative path.
                     */
                    Path relativePath = Path.of(entryName).normalize();

                    if (relativePath.isAbsolute()
                            || relativePath.startsWith("..")) {
                        throw new IOException(
                                "Invalid path in ZIP: " + entryName
                        );
                    }

                    Path output = root.resolve(relativePath).normalize();

                    if (!output.startsWith(root)) {
                        throw new IOException(
                                "ZIP entry escapes the project directory: "
                                        + entryName
                        );
                    }

                    /*
                     * STEP 3: Skip irrelevant directories.
                     */
                    if (shouldIgnore(entryName)) {
                        continue;
                    }

                    /*
                     * STEP 4: Handle directories.
                     */
                    if (entry.isDirectory()) {
                        Files.createDirectories(output);
                        continue;
                    }

                    /*
                     * STEP 5: Extract only relevant files.
                     */
                    if (!shouldInclude(relativePath)) {
                        continue;
                    }

                    /*
                     * STEP 6: Reject duplicate file paths.
                     */
                    if (!extractedPaths.add(output)) {
                        throw new IOException(
                                "Duplicate file path in ZIP: " + entryName
                        );
                    }

                    Files.createDirectories(output.getParent());

                    long fileBytes = 0;

                    /*
                     * CREATE_NEW prevents overwriting an existing file.
                     */
                    try (var out = Files.newOutputStream(
                            output,
                            StandardOpenOption.CREATE_NEW,
                            StandardOpenOption.WRITE
                    )) {
                        byte[] buffer = new byte[8192];
                        int read;

                        while ((read = zip.read(buffer)) != -1) {

                            fileBytes += read;
                            totalBytes += read;

                            if (fileBytes > MAX_FILE_SIZE) {
                                throw new IOException(
                                        "A project file exceeds the "
                                                + "50 MB limit: " + entryName
                                );
                            }

                            if (totalBytes > MAX_TOTAL_SIZE) {
                                throw new IOException(
                                        "Extracted project files exceed "
                                                + "the 2 GB limit."
                                );
                            }

                            out.write(buffer, 0, read);
                        }
                    }

                } finally {
                    zip.closeEntry();
                }
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

        Path fileNamePath = relativePath.getFileName();

        if (fileNamePath == null) {
            return false;
        }

        String fileName = fileNamePath.toString()
                .toLowerCase(Locale.ROOT);

        return fileName.endsWith(".java")
                || INCLUDED_FILES.contains(fileName);
    }
}
