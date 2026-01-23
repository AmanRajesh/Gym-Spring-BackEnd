package com.automlhybrid.gymspringbackend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class PipelineRequest {
    private String fileKey;          // "raw-data/user-1/data.csv"
    private List<PreprocessingStep> preprocessing;    // ["DROP_NULLS", "LOG_TRANSFORM"]
    private String targetColumn;
    private String modelType;        // "RandomForest", "CNN"
    private Map<String, Object> hyperparameters;

    @Data
    public static class PreprocessingStep {
        private String action;       // e.g., "FILL_MEDIAN"
        private List<String> columns; // e.g., ["Age", "Salary"]
    }
    public String getFileKey() { return fileKey; }
    public void setFileKey(String fileKey) { this.fileKey = fileKey; }

    public List<PreprocessingStep> getPreprocessing() {
        return preprocessing;
    }

    public void setPreprocessing(List<PreprocessingStep> preprocessing) {
        this.preprocessing = preprocessing;
    }

    public String getTargetColumn() { return targetColumn; }
    public void setTargetColumn(String targetColumn) { this.targetColumn = targetColumn; }

    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }

    // UPDATE these methods!
    public Map<String, Object> getHyperparameters() { return hyperparameters; }
    public void setHyperparameters(Map<String, Object> hyperparameters) { this.hyperparameters = hyperparameters; }
}
