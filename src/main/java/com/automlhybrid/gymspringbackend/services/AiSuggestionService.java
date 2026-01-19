package com.automlhybrid.gymspringbackend.services; // <--- CHANGED

import com.automlhybrid.gymspringbackend.clients.MlEngineClient;
import com.automlhybrid.gymspringbackend.dto.AnalysisResponse;
import com.automlhybrid.gymspringbackend.dto.PipelineRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiSuggestionService {

    private final MlEngineClient mlEngineClient;
    // private final LlamaClient llamaClient; // Assuming you have an AI client wrapper

    /**
     * 1. Asks Python for data stats.
     * 2. Builds a prompt for Llama.
     * 3. Returns the AI's advice.
     */
    public String getAiAdvice(String fileKey, List<String> currentActions) {

        // STEP 1: Get Statistical Facts from Python (The "Muscle")
        PipelineRequest req = new PipelineRequest();
        req.setFileKey(fileKey);
        req.setActions(currentActions);

        // This Feign call hits http://ml-engine:8000/analyze
        AnalysisResponse stats = mlEngineClient.analyzeFile(req);

        // STEP 2: Construct the Prompt (The "Brain")
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an AutoML expert. Analyze this dataset metadata:\n");
        prompt.append("- Row Count: ").append(stats.getRowCount()).append("\n");

        // Add dynamic observations based on Python's analysis
        if (stats.hasSkewedColumns()) {
            prompt.append("- Highly Skewed Columns: ").append(stats.getSkewedColumns()).append("\n");
        }
        if (stats.hasMissingValues()) {
            prompt.append("- Columns with Nulls: ").append(stats.getColumnsWithNulls()).append("\n");
        }

        prompt.append("\nCurrent Actions Applied: ").append(currentActions).append("\n");
        prompt.append("Suggest the next best preprocessing step from: [LOG_TRANSFORM, FILL_MEDIAN, STANDARD_SCALE, ONE_HOT_ENCODE].");

        // STEP 3: Call your LLM (Llama / OpenAI / Ollama)
        // return llamaClient.generate(prompt.toString());

        // For testing without a real GPU LLM yet:
        return mockLlamaResponse(stats);
    }

    private String mockLlamaResponse(AnalysisResponse stats) {
        if (!stats.getSkewedColumns().isEmpty()) {
            return "I detected high skewness in " + stats.getSkewedColumns() + ". I recommend applying a **Log Transform** to normalize distributions.";
        }
        if (!stats.getColumnsWithNulls().isEmpty()) {
            return "There are missing values in " + stats.getColumnsWithNulls() + ". You should use **Fill Median** or **Drop Rows**.";
        }
        return "The data looks clean! You are ready to train a model.";
    }
}