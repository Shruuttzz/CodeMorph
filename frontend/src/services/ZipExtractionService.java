package com.codemorph.backend.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
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


    public void extractRelevantFiles(
            InputStream inputStream,
            Path destination
    ) throws IOException {

        try (
                ZipInputStream zipInputStream =
                        new ZipInputStream(inputStream)
        ) {

            ZipEntry entry;

            while (
                    (entry = zipInputStream.getNextEntry())
                            != null
            ) {

                String entryName =
                        normalizeEntryName(entry.getName());


                /*
                 * Ignore directories completely.
                 */
                if (entry.isDirectory()) {
                    continue;
                }


                /*
                 * Ignore unwanted project directories.
                 */
                if (shouldIgnore(entryName)) {
                    continue;
                }


                /*
                 * Only extract files that are useful
                 * for CodeMorph analysis.
                 */
                if (!isRelevantFile(entryName)) {
                    continue;
                }


                Path outputPath =
                        destination.resolve(entryName)
                                .normalize();


                /*
                 * ZIP SLIP protection.
                 *
                 * Never allow a ZIP entry such as:
                 *
                 * ../../some-file
                 *
                 * to escape the temporary directory.
                 */
                if (!outputPath.startsWith(
                        destination.normalize()
                )) {

                    throw new IOException(
                            "Unsafe ZIP entry: "
                                    + entryName
                    );
                }


                Files.createDirectories(
                        outputPath.getParent()
                );


                try (
                        OutputStream outputStream =
                                Files.newOutputStream(
                                        outputPath,
                                        StandardOpenOption.CREATE,
                                        StandardOpenOption.TRUNCATE_EXISTING
                                )
                ) {

                    zipInputStream.transferTo(
                            outputStream
                    );
                }
            }
        }
    }


    private boolean shouldIgnore(
            String entryName
    ) {

        String[] parts =
                entryName.split("/");


        for (String part : parts) {

            if (
                    IGNORED_DIRECTORIES.contains(
                            part
                    )
            ) {

                return true;
            }
        }

        return false;
    }


    private boolean isRelevantFile(
            String entryName
    ) {

        String lower =
                entryName.toLowerCase();


        /*
         * Java source is the most important
         * input for CodeMorph.
         */
        if (lower.endsWith(".java")) {
            return true;
        }


        /*
         * Keep common build/config files if
         * CodeMorph needs them later.
         */
        if (
                lower.endsWith("build.gradle") ||
                        lower.endsWith("build.gradle.kts") ||
                        lower.endsWith("pom.xml") ||
                        lower.endsWith("settings.gradle") ||
                        lower.endsWith("settings.gradle.kts")
        ) {

            return true;
        }


        /*
         * Everything else is currently
         * unnecessary for Java AST analysis.
         */
        return false;
    }


    private String normalizeEntryName(
            String entryName
    ) {

        return entryName
                .replace('\\', '/')
                .replaceAll("^/+", "");
    }
}