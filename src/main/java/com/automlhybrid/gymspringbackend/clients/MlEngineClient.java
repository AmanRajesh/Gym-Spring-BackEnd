package com.automlhybrid.gymspringbackend.clients;

import com.automlhybrid.gymspringbackend.dto.AnalysisResponse;
import com.automlhybrid.gymspringbackend.dto.PipelineRequest;
import com.automlhybrid.gymspringbackend.dto.PreviewResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// "ml-engine" must match the Docker Compose Service Name
// URL reads from application.properties
@FeignClient(name = "ml-engine", url = "${app.services.ml-engine-url}")
public interface MlEngineClient {

    @PostMapping("/preview")
    PreviewResponse getPreview(@RequestBody PipelineRequest request);
    @PostMapping("/analyze")
    AnalysisResponse analyzeFile(@RequestBody PipelineRequest request);

    // Future: Add /analyze for AI stats
}
