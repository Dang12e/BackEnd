package com.testBackendDatabase.demo.Chatbot.Tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testBackendDatabase.demo.Chatbot.ChatbotTool;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AppUsageContextTool implements ChatbotTool {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getToolName() {
        return "get_app_usage_and_policy_context";
    }

    @Override
    public Map<String, Object> getToolDefinition() {
        return Map.of(
            "type", "function",
            "function", Map.of(
                "name", getToolName(),
                "description", "MANDATORY to call this tool when the user asks about app capabilities, guidelines, policies, system rules, or actions like how to BOOK, CANCEL, or EXCHANGE tickets.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of()
                )
            )
        );
    }

    @Override
public String execute(String argumentsJson) {
    try {
        // Guide the chatbot on how to interpret information and handle requests
        String appUsage = "You are the CINEVERSE Movie Assistant. Only process short queries related to: searching movies, checking showtimes, viewing ticket status, or cinema information. " +
        "For showtime requests, identify: movie name, city/theater, and desired date/time. " +
        "IMPORTANT: This application DOES NOT SUPPORT canceling or exchanging tickets directly via chat. If the user wants to CANCEL or EXCHANGE tickets, instruct them to contact the Cineverse Customer Support HOTLINE immediately for assistance. " + // Thêm dấu cách ở cuối
        "To book tickets: click on your desired movie, click on showtime, choose available seats and confirm."; // Sửa lỗi chính tả avaible -> available

        // Security policy and bot response guidelines
        String policy = "Do not request or store personal data or private account details. " +
                "Keep responses concise and direct to reduce token usage and speed up response times. " +
                "When declining booking, cancellation, or exchange requests, remain polite and clearly direct the user to the hotline.";

        return objectMapper.writeValueAsString(Map.of(
            "status", "success",
            "tool", getToolName(),
            "app_usage", appUsage,
            "policy", policy,
            "recommendations", Map.of(
                "example_1", "Find showtimes for Doraemon in Hanoi tomorrow.",
                "example_2", "Check available seats for Avengers at CGV My Dinh tonight.",
                "example_3", "What is the next showtime for Spider-Man in Haiphong?",
                "example_4", "How can I cancel my ticket?",
                "example_5","Cách đặt vé"
            )
        ));
    } catch (JsonProcessingException e) {
        return "{\"status\": \"error\", \"message\": \"Could not serialize usage context.\"}";
    }
}
}
