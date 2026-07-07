package com.testBackendDatabase.demo.Chatbot;


import java.util.Map;

public interface ChatbotTool {
    String getToolName();                       // Tên của Tool (ví dụ: get_current_weather)
    Map<String, Object> getToolDefinition();    // Cấu trúc JSON khai báo với AI
    String execute(String argumentsJson);       // Hàm thực thi code Java local
}
