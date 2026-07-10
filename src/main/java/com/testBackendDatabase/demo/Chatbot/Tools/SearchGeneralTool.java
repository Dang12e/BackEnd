package com.testBackendDatabase.demo.Chatbot.Tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testBackendDatabase.demo.model.ShowTime;
import com.testBackendDatabase.demo.Chatbot.ChatbotTool;
import com.testBackendDatabase.demo.Repository.ShowRoomRepository;
import com.testBackendDatabase.demo.Repository.ShowTimeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SearchGeneralTool implements ChatbotTool {

    @Autowired
    private ShowTimeRepository showTimeRepository; // Tiêm JPA Repository vào

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getToolName() {
        return "search_general_system";
    }

    @Override
    public Map<String, Object> getToolDefinition() {
        return Map.of(
            "type", "function",
            "function", Map.of(
                "name", getToolName(),
                "description", "Search for movies, showtimes, theater locations, or check booking status based on flexible user descriptions.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "search_type", Map.of(
                            "type", "string",
                            "enum", java.util.List.of("MOVIES_AND_SHOWTIMES", "CHECK_BOOKING"),
                            "description", "Categorize if the user wants to search for movies/showtimes OR check a booking/order status."
                        ),
                        "keyword", Map.of(
                            "type", "string",
                            "description", "Partial movie title, character name, or general keyword. E.g., 'Lat', 'Batman'."
                        ),
                        "time_frame", Map.of(
                            "type", "string",
                            "description", "Time descriptions like 'tonight', 'evening', '7pm', 'tomorrow'."
                        ),
                        "location", Map.of(
                            "type", java.util.List.of("string", "null"),
                            "description", "Theater name or city mentioned. E.g., 'Hanoi', 'CGV Ba Dinh'."
                        ),
                        "booking_id", Map.of(
                            "type", "string",
                            "description", "The order ID or booking code if the user is checking ticket status."
                        )
                    )
                    
                )
            )
        );
    }

    @Override
    public String execute(String argumentsJson) {
        if (argumentsJson.contains("CHECK_BOOKING")) {
            return "{\"status\": \"success\"}"; 
        } else {
            return executeSearchMoviesAndShowtimes(argumentsJson);
        }
    }

    /**
     * HÀM THỰC THI SQL ĐỘNG BẰNG HIBERNATE JPA SPECIFICATION
     */
    private String executeSearchMoviesAndShowtimes(String argumentsJson) {
        System.out.println("-> Bắt đầu xây dựng JPA Specification động từ dữ liệu AI...");

        try {
            JsonNode rootNode = objectMapper.readTree(argumentsJson);

            // Tạo một Specification động (Nơi chứa các logic AND, OR)
            Specification<ShowTime> spec = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                // 1. Điều kiện TÊN PHÌM (Keyword) - Cần Join bảng Showtime với bảng Movie
                if (rootNode.has("keyword") && !rootNode.get("keyword").asText().isEmpty()) {
                    String keyword = rootNode.get("keyword").asText();
                    Join<Object, Object> movieJoin = root.join("movie"); // "movie" là tên thuộc tính @ManyToOne trong Entity Showtime
                    predicates.add(criteriaBuilder.like(movieJoin.get("title"), "%" + keyword + "%"));
                }

                // 2. Điều kiện TÊN RẠP (Location) - Join từ ShowTime -> ShowRoom -> Cinema
                if (rootNode.has("location") && !rootNode.get("location").asText().isEmpty()) {
                    String location = rootNode.get("location").asText();
                    Join<Object, Object> showRoomJoin = root.join("showRoom"); // join đến ShowRoom
                    Join<Object, Object> theaterJoin = showRoomJoin.join("cinema"); // sau đó join đến Cinema
                    predicates.add(criteriaBuilder.like(theaterJoin.get("name"), "%" + location + "%"));
                    predicates.add(criteriaBuilder.like(theaterJoin.get("address"), "%" + location + "%"));
                }

                // 3. Điều kiện THỜI GIAN (Time Frame)
                if (rootNode.has("time_frame") && !rootNode.get("time_frame").asText().isEmpty()) {
                    String timeFrame = rootNode.get("time_frame").asText();
                    
                    if (timeFrame.equalsIgnoreCase("tonight") || timeFrame.contains("hôm nay")) {
                        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
                        LocalDateTime endOfToday = LocalDate.now().atTime(23, 59, 59);
                        
                        // Lọc các suất chiếu nằm trong ngày hôm nay
                        predicates.add(criteriaBuilder.between(root.get("startTime"), startOfToday, endOfToday));
                    }
                }

                // Gộp chung tất cả các điều kiện lại bằng phép toán AND
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            // 4. Thực thi câu truy vấn động xuống Database thông qua JPA Repository
            List<ShowTime> showtimes = showTimeRepository.findAll(spec);

            // 5. Khớp định dạng dữ liệu (Chuyển list Entity thành list Map để viết thành JSON gọn gàng)
            List<Map<String, Object>> resultList = showtimes.stream().map(s -> Map.<String, Object>of(
    "movie", s.getMovie().getTitle(),
    "time", s.getStartTime().toString(),
    "cinema", s.getShowRoom().getCinema().getName(),
    "adress",s.getShowRoom().getCinema().getAddress(),
    "rating",s.getMovie().getRating()
   
)).collect(Collectors.toList());

            Map<String, Object> finalResult = Map.of(
                "status", "success",
                "count", resultList.size(),
                "results", resultList
            );
            System.out.println("FINALLLLLRESULT: "+ finalResult);

            return objectMapper.writeValueAsString(finalResult);

        } catch (Exception e) {
            System.err.println("Lỗi thực thi JPA Specification: " + e.getMessage());
            return "{\"status\": \"error\", \"message\": \"Lỗi truy vấn dữ liệu thông qua JPA\"}";
        }
    }
}