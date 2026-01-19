package com.automlhybrid.gymspringbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class AnalysisResponse {
    private int rowCount;
    private List<String> skewedColumns;      // e.g. ["Income", "Price"]
    private List<String> columnsWithNulls;   // e.g. ["Age"]

    // Helper methods for cleaner logic in the service
    public boolean hasSkewedColumns() {
        return skewedColumns != null && !skewedColumns.isEmpty();
    }

    public boolean hasMissingValues() {
        return columnsWithNulls != null && !columnsWithNulls.isEmpty();
    }
}