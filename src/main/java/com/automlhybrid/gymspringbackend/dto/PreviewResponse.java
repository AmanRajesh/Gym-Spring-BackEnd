package com.automlhybrid.gymspringbackend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PreviewResponse {
    private List<Map<String, String>> previewRows; // The table data for UI
    private List<String> columns;
    private boolean isReadyForTraining;
    private String error; // In case Python crashes
}
