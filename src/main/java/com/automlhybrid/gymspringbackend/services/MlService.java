package com.automlhybrid.gymspringbackend.services;

import com.automlhybrid.gymspringbackend.clients.MlEngineClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MlService {

    @Autowired
    private MlEngineClient mlEngineClient;

    /**
     * Calls Python API to check for clean data.
     * Returns the URL string if found, or null if not ready/failed.
     */
    public String getCleanDataUrl(String fileKey) {
        try {
            // 1. Call the Python Microservice
            Map<String, Object> response = mlEngineClient.getCleanedData(fileKey);

            // 2. Inspect the Response
            if (response != null && "FOUND".equals(response.get("status"))) {
                return (String) response.get("downloadUrl");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error fetching clean data: " + e.getMessage());
        }

        // Return null if file is missing, still processing, or error occurred
        return null;
    }
}
