package com.automlhybrid.gymspringbackend.analysis;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileProcessingService {

    public Map<String, Object> processFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new IllegalArgumentException("Invalid filename");

        Map<String, Object> result = new HashMap<>();
        result.put("filename", filename);

        if (filename.toLowerCase().endsWith(".zip")) {
            // Smart Zip Analysis (Images/Audio)
            result.putAll(extractZipSummary(file));
        } else if (filename.toLowerCase().endsWith(".csv") || filename.toLowerCase().endsWith(".json")) {
            // Smart Tabular Analysis
            result.put("type", "TABULAR");
            result.put("summary", extractTabularSummary(file));
        } else {
            throw new IllegalArgumentException("Unsupported file format.");
        }
        return result;
    }

    // --- 1. SMART TABULAR SCANNER ---
    private Map<String, Object> extractTabularSummary(MultipartFile file) throws Exception {
        Map<String, Object> summary = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            // A. Headers
            List<String> headers = csvParser.getHeaderNames();
            summary.put("columns", headers);
            summary.put("column_count", headers.size());

            // B. Scan First 100 Rows for Profiling
            List<Map<String, String>> preview = new ArrayList<>();
            Map<String, Integer> nullCounts = new HashMap<>();
            Map<String, Set<String>> uniqueValues = new HashMap<>();
            Map<String, List<Double>> numericSamples = new HashMap<>();
            int rowCount = 0;

            for (CSVRecord record : csvParser) {
                // Save first 5 rows for preview
                if (rowCount < 5) preview.add(record.toMap());

                // Profile first 100 rows for Missing Values & Types
                if (rowCount < 100) {
                    for (String col : headers) {
                        String val = record.isSet(col) ? record.get(col) : "";
                        if (val == null || val.trim().isEmpty()) {
                            nullCounts.put(col, nullCounts.getOrDefault(col, 0) + 1);
                        }
                    }
                }
                rowCount++;
            }

            summary.put("preview", preview);
            summary.put("total_rows_estimated", rowCount); // Note: This only counts what we read if we break early

            // C. Preprocessing Advice for AI
            List<String> qualityIssues = new ArrayList<>();
            for (String col : headers) {
                // Missing Value Advice
                if (nullCounts.getOrDefault(col, 0) > 0) {
                    qualityIssues.add("Column '" + col + "' has missing values. Suggest: 'Drop Nulls' or 'Fill Median'.");
                }

                // Skewness Advice (Clue for Log Transform)
                if (numericSamples.containsKey(col)) {
                    List<Double> samples = numericSamples.get(col);
                    double max = Collections.max(samples);
                    double mean = samples.stream().mapToDouble(d -> d).average().orElse(0.0);
                    if (mean > 0 && (max / mean) > 5.0) {
                        qualityIssues.add("Column '" + col + "' is highly skewed (Max=" + max + ", Mean=" + mean + "). Suggest: 'Log Transform'.");
                    }
                }

                // Cardinality Advice (Clue for One-Hot Encoding)
                int uniqueCount = uniqueValues.getOrDefault(col, new HashSet<>()).size();
                if (!numericSamples.containsKey(col) && uniqueCount > 2 && uniqueCount < 10) {
                    qualityIssues.add("Column '" + col + "' is categorical with " + uniqueCount + " types. Suggest: 'One-Hot Encode'.");
                }
            }

            summary.put("quality_alerts", qualityIssues);
        }
        return summary;
    }

    // --- 2. SMART ZIP SCANNER (Visual/Audio) ---
    private Map<String, Object> extractZipSummary(MultipartFile file) throws Exception {
        Map<String, Object> summary = new HashMap<>();
        List<String> samples = new ArrayList<>();
        int imageCount = 0;
        int audioCount = 0;

        // Resolution Check
        int widthSum = 0;
        int heightSum = 0;
        int dimensionsChecked = 0;

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory() || entry.getName().startsWith(".") || entry.getName().contains("__MACOSX")) continue;

                String name = entry.getName().toLowerCase();
                String simpleName = name.substring(name.lastIndexOf("/") + 1);

                // A. Image Logic
                if (isImage(name)) {
                    imageCount++;
                    if (samples.size() < 5) samples.add(simpleName);

                    // Check Dimensions of the first 5 images (Preprocessing Check)
                    if (dimensionsChecked < 5) {
                        // Note: ImageIO wraps the stream, so we must be careful not to close the ZIP stream
                        // In a real production app, we would copy the entry to a ByteArrayInputStream first.
                        // For now, we skip deep pixel reading to keep it fast and safe.
                        dimensionsChecked++;
                    }
                }
                // B. Audio Logic
                else if (isAudio(name)) {
                    audioCount++;
                    if (samples.size() < 5) samples.add(simpleName);
                }
            }
        }

        // C. Classification
        if (audioCount > imageCount) {
            summary.put("type", "AUDIO");
            summary.put("summary", Map.of(
                    "count", audioCount,
                    "samples", samples,
                    "preprocessing_note", "Audio files detected. Check sampling rates."
            ));
        } else {
            summary.put("type", "VISUAL");
            summary.put("summary", Map.of(
                    "count", imageCount,
                    "samples", samples,
                    "preprocessing_note", "Images detected. Ensure consistent resolution."
            ));
        }
        return summary;
    }

    private boolean isImage(String name) {
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".webp");
    }

    private boolean isAudio(String name) {
        return name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".flac");
    }
}