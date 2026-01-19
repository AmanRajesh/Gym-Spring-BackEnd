package com.automlhybrid.gymspringbackend.controllers;

import com.automlhybrid.gymspringbackend.clients.MlEngineClient;
import com.automlhybrid.gymspringbackend.dto.PipelineRequest;
import com.automlhybrid.gymspringbackend.dto.PreviewResponse;
import com.automlhybrid.gymspringbackend.services.GpuJobProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/orchestrator")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Allow React to call this
public class OrchestratorController {

    private final MlEngineClient mlEngineClient;
    private final GpuJobProducer gpuJobProducer;

    // 1. Synchronous: Get Preview from Python (CPU)
    @PostMapping("/preview")
    public PreviewResponse getPreview(@RequestBody PipelineRequest request) {
        System.out.println("Requesting preview from Python for: " + request.getFileKey());
        return mlEngineClient.getPreview(request);
    }

    // 2. Asynchronous: Start Heavy Training (GPU)
    @PostMapping("/train")
    public String startTraining(@RequestBody PipelineRequest request) {
        // Push to Queue
        gpuJobProducer.submitJob(Map.of(
                "file_key", request.getFileKey(),
                "actions", request.getActions(),
                "model", request.getModelType()
        ));
        return "Training Started! You will be notified via WebSocket/Email.";
    }
}