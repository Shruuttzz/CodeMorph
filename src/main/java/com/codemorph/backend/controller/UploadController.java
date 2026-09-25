package com.codemorph.backend.controller;
import com.codemorph.backend.model.ProjectSummary;
import com.codemorph.backend.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<ProjectSummary> uploadProject(
            @RequestParam("file") MultipartFile file) {

        try {
            ProjectSummary summary = uploadService.processZip(file);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}