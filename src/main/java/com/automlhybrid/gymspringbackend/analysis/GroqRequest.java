package com.automlhybrid.gymspringbackend.analysis;

import java.util.ArrayList;
import java.util.List;

public class GroqRequest {
    private String model;
    private List<Message> messages;
    private double temperature;

    public GroqRequest(String model, String systemInstructions, String userPrompt) {
        this.model = model;
        this.temperature = 0.5; // Lower temperature for more consistent architectural advice
        this.messages = new ArrayList<>();
        // 1. System role sets the "Hybrid AutoML Expert" persona
        this.messages.add(new Message("system", systemInstructions));
        // 2. User role provides the metadata
        this.messages.add(new Message("user", userPrompt));
    }

    public String getModel() { return model; }
    public List<Message> getMessages() { return messages; }
    public double getTemperature() { return temperature; }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
    }
}