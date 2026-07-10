package com.testBackendDatabase.demo.Chatbot;

import com.testBackendDatabase.demo.Chatbot.ChatContextService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.testBackendDatabase.demo.model.MessageResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    private final ChatContextService chatContextService;

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String url;

    private final RestTemplate restTemplate = new RestTemplate();
    private final List<ChatbotTool> availableTools;
    @Value("${chat.guide}")
    private String guide;

    @Value("${chat.model}")
    private String model;

    

    // Spring Boot sẽ tự động quét và nạp tất cả các Class implement ChatbotTool vào List này
    public ChatbotService(List<ChatbotTool> availableTools, ChatContextService chatContextService) {
        this.availableTools = availableTools;
        this.chatContextService = chatContextService;
    }

    public String getChatResponse(String userMessage, List<MessageResponse> context,Long userID) {
        HttpHeaders headers = buildHeaders();
        
        // Tạo lịch sử trò chuyện (Chứa System Prompt và tin nhắn của User)
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", guide));
        for (MessageResponse chat : context) {
    messages.add(Map.of("role", "user", "content", chat.getUserMessage()));
    messages.add(Map.of("role", "assistant", "content", chat.getAiResponse()));
}
        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> requestBody = buildRequestBody(messages);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Lượt gọi 1: Gửi câu hỏi + Định nghĩa Tool lên AI
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            String aIresponse= handleAIResponse(response, messages, headers);
            if (aIresponse == null) {
                aIresponse = "{\"status\": \"error\", \"message\": \"No response from AI due to internal error.\"}";
            }
            chatContextService.saveChatHistory(userID, userMessage, aIresponse);
             return aIresponse;
        } catch (HttpClientErrorException e) {
            System.out.println("Chi tiết lỗi API: " + e.getResponseBodyAsString());
            return "Lỗi API: " + e.getStatusCode();
        } catch (Exception e) {
            return "Lỗi hệ thống: " + e.getMessage();
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        return headers;
    }

    private Map<String, Object> buildRequestBody(List<Map<String, Object>> messages) {
        // Gom tất cả các cấu hình định nghĩa của các Tool hiện có
        List<Map<String, Object>> toolDefinitions = new ArrayList<>();
        for (ChatbotTool tool : availableTools) {
            toolDefinitions.add(tool.getToolDefinition());
        }

        return Map.of(
            "model", model,
            "include_reasoning", false, // Tên model chạy ổn định trên Groq
            "messages", messages,
            "tools", toolDefinitions
        );
    }

    private String handleAIResponse(Map<String, Object> response, List<Map<String, Object>> messages, HttpHeaders headers) {
        if (response == null || !response.containsKey("choices")) return "No response received";

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> aiMessage = (Map<String, Object>) choices.get(0).get("message");

        // KIỂM TRA: AI có đòi gọi Tool không?
        if (aiMessage.containsKey("tool_calls") && aiMessage.get("tool_calls") != null) {
            List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) aiMessage.get("tool_calls");
            Map<String, Object> firstCall = toolCalls.get(0);
            
            String targetFunctionName = (String) ((Map<String, Object>) firstCall.get("function")).get("name");
            String arguments = (String) ((Map<String, Object>) firstCall.get("function")).get("arguments");
            String toolCallId = (String) firstCall.get("id");

            // Tìm kiếm Tool tương ứng trong danh sách local để chạy
            String toolResult = "Error: required tool not found";
            for (ChatbotTool tool : availableTools) {
                if (tool.getToolName().equals(targetFunctionName)) {
                    toolResult = tool.execute(arguments); // Thực thi hàm Java
                    break;
                }
            }

            // Đồng bộ lại ngữ cảnh: Thêm lệnh gọi tool của AI và kết quả thô vào lịch sử nhắn tin
            messages.add(aiMessage); 
            messages.add(Map.of(
                "role", "tool",
                "tool_call_id", toolCallId,
                "name", targetFunctionName,
                "content", toolResult
            ));

            List<Map<String, Object>> toolDefinitions = new ArrayList<>();
        for (ChatbotTool tool : availableTools) {
            toolDefinitions.add(tool.getToolDefinition());
        }

            // Lượt gọi 2: Gửi toàn bộ lịch sử đã có kết quả tool lên cho AI tổng hợp
            Map<String, Object> finalRequestBody = Map.of(
                "model", model,
                "include_reasoning", false,
                "messages", messages,
                "tools", toolDefinitions
            );

            HttpEntity<Map<String, Object>> finalEntity = new HttpEntity<>(finalRequestBody, headers);
            Map<String, Object> finalResponse = restTemplate.postForObject(url, finalEntity, Map.class);
            
            List<Map<String, Object>> finalChoices = (List<Map<String, Object>>) finalResponse.get("choices");
            Map<String, Object> finalAiMessage = (Map<String, Object>) finalChoices.get(0).get("message");
            
            return (String) finalAiMessage.get("content");
        }

        // Nếu AI không gọi tool, trả về câu trả lời thuần túy luôn
        return (String) aiMessage.get("content");
    }
}