package com.automlhybrid.gymspringbackend.analysis; // or com.AutoML_Hybrid.orchestrator.controllers

import com.automlhybrid.gymspringbackend.clients.MlEngineClient;
import com.automlhybrid.gymspringbackend.dto.AnalysisResponse;
import com.automlhybrid.gymspringbackend.dto.PipelineRequest;
import com.automlhybrid.gymspringbackend.storage.StorageService; // New Service we will create
import com.automlhybrid.gymspringbackend.services.AiSuggestionService; // The updated service
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor // Generates constructor for final fields

public class AnalysisController {

    private final StorageService storageService; // Replaces FileProcessingService
    private final AiSuggestionService aiService;
    private final MlEngineClient mlEngineClient;

    @GetMapping("/")
    public String healthCheck() {
        return "✅ Orchestrator is Running!";
    }

    @PostMapping("/upload")
    public ResponseEntity<?> analyzeFile(@RequestParam("file") MultipartFile file) {
        try {
            // STEP 1: Save File & Get Key (Don't open it in Java)
            // If MinIO is off, this saves to a local folder
            String fileKey = storageService.uploadFile(file);

            PipelineRequest analysisRequest = new PipelineRequest();
            analysisRequest.setFileKey(fileKey);

            AnalysisResponse stats = mlEngineClient.analyzeFile(analysisRequest);
            // STEP 2: Call AI Service (Which calls Python)
            // We pass an empty list of actions because it's a fresh upload
            String suggestion = aiService.getAiAdvice(fileKey, List.of());

            stats.setAiSuggestion(suggestion);
            stats.setFileKey(fileKey);
            // STEP 3: Return Response
            return ResponseEntity.ok(Map.of(
                    "fileKey", fileKey,
                    "filename", file.getOriginalFilename(),
                    "suggestion", suggestion
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}