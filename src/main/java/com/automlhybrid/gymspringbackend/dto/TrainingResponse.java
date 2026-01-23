package com.automlhybrid.gymspringbackend.dto;

import java.util.Map;

public class TrainingResponse {

    private String status;         // "COMPLETED", "QUEUED", "ERROR"
    private String message;        // e.g., "Training Completed using RandomForest"
    private String jobId;          // Optional: For tracking async worker jobs
    private String modelUrl;       // 🔗 Link to download the .pkl model
    private String cleanedDataUrl; // 🔗 Link to download the clean .csv data

    // We use a Map because metrics change based on the model
    // (e.g., Classification has "Accuracy", Regression has "RMSE")
    private Map<String, Object> metrics;

    // --- GETTERS AND SETTERS ---

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getModelUrl() { return modelUrl; }
    public void setModelUrl(String modelUrl) { this.modelUrl = modelUrl; }

    public String getCleanedDataUrl() { return cleanedDataUrl; }
    public void setCleanedDataUrl(String cleanedDataUrl) { this.cleanedDataUrl = cleanedDataUrl; }

    public Map<String, Object> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
}
