
package com.codemorph.backend.controller;

import com.codemorph.backend.model.ProjectSummary;
import com.codemorph.backend.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProject(
            @RequestParam("file") MultipartFile file) {

        try {
            ProjectSummary summary = uploadService.processZip(file);
            return ResponseEntity.ok(summary);

        } catch (IllegalArgumentException e) {

            // Invalid input, such as a ZIP with no Java source files.
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );

        } catch (IOException e) {

            // Invalid ZIP, extraction failure, or file-size limit exceeded.
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "message",
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Unable to process the uploaded ZIP file."
                    )
            );

        } catch (Exception e) {

            // Unexpected analysis failure.
            e.printStackTrace();

            return ResponseEntity.internalServerError().body(
                    Map.of(
                            "message",
                            "Project analysis failed. Check the backend logs."
                    )
            );
        }
    }
}
