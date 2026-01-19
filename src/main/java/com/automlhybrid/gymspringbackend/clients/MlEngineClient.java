package com.automlhybrid.gymspringbackend.clients;

import com.automlhybrid.gymspringbackend.dto.AnalysisResponse;
import com.automlhybrid.gymspringbackend.dto.PipelineRequest;
import com.automlhybrid.gymspringbackend.dto.PreviewResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

// "ml-engine" is the service name in Docker Compose
@FeignClient(name = "ml-engine", url = "${app.services.ml-engine-url}")
public interface MlEngineClient {

    @PostMapping("/preview")
    PreviewResponse getPreview(@RequestBody PipelineRequest request);

    @PostMapping("/analyze")
    AnalysisResponse analyzeFile(@RequestBody PipelineRequest request);

    /**
     * The Decision Fork:
     * Sends the training config to Python (CPU).
     * Python decides:
     * A) Train immediately (Returns { status: "COMPLETED", accuracy: 0.95 })
     * B) Queue for GPU (Returns { status: "QUEUED", message: "Job sent to GPU" })
     */
    @PostMapping("/process-job")
    Map<String, Object> startTrainingProcess(@RequestBody PipelineRequest request);
}