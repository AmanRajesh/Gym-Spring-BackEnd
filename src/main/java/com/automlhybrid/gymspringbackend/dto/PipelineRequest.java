package com.automlhybrid.gymspringbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class PipelineRequest {
    private String fileKey;          // "raw-data/user-1/data.csv"
    private List<String> actions;    // ["DROP_NULLS", "LOG_TRANSFORM"]
    private String targetColumn;
    private String modelType;        // "RandomForest", "CNN"
    private Double hyperparameters;
}
