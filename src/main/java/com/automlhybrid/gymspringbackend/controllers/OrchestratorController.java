package com.automlhybrid.gymspringbackend.controllers;

import com.automlhybrid.gymspringbackend.clients.MlEngineClient;
import com.automlhybrid.gymspringbackend.dto.PipelineRequest;
import com.automlhybrid.gymspringbackend.dto.PreviewResponse;
import com.automlhybrid.gymspringbackend.services.MlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/orchestrator")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class OrchestratorController {

    private final MlEngineClient mlEngineClient;
    @Autowired
    private MlService mlService;

    // 1. Preview Data (Synchronous CPU)
    @PostMapping("/preview")
    public PreviewResponse getPreview(@RequestBody PipelineRequest request) {
        return mlEngineClient.getPreview(request);
    }

    // 2. Start Training (The "Decision Fork" Logic)
    // We no longer decide HERE if it goes to the GPU. We let the ML Engine decide.
    @PostMapping("/train")
    public ResponseEntity<Map<String, Object>> startTraining(@RequestBody PipelineRequest request) {
        System.out.println("\uD83D\uDE80 Forwarding training request to ML Engine for: " + request.getFileKey());

        // Call Python Service
        Map<String, Object> response = mlEngineClient.startTrainingProcess(request);

        return ResponseEntity.ok(response);
    }
    // for downloading the file , service in ml service
    @GetMapping("/download-clean/{fileKey}")
    public ResponseEntity<?> downloadCleanData(@PathVariable String fileKey) {

        // 1. Ask Service for the URL
        String downloadUrl = mlService.getCleanDataUrl(fileKey);

        // 2. Handle the result
        if (downloadUrl != null) {
            // Return JSON: { "url": "http://minio..." }
            return ResponseEntity.ok(Collections.singletonMap("url", downloadUrl));
        } else {
            // Return 404 if not ready yet
            return ResponseEntity.status(404)
                    .body(Collections.singletonMap("error", "File not found or still processing"));
        }
    }

}