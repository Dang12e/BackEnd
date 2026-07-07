package com.testBackendDatabase.demo.Chatbot.Tools;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testBackendDatabase.demo.Chatbot.ChatbotTool;
import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.OrderRepository;
import com.testBackendDatabase.demo.model.Account;
import com.testBackendDatabase.demo.model.Order;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OrderTicketSearchTool implements ChatbotTool {

    private final ObjectMapper objectMapper = new ObjectMapper();

    
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    public OrderTicketSearchTool(OrderRepository orderRepository, AccountRepository accountRepository)
    {
         this.orderRepository= orderRepository;
         this.accountRepository = accountRepository;
    }

    // Giả lập lấy account_id ẩn từ JWT Token dưới Backend Java
    private Long getAccountIdFromToken() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        Account account= accountRepository.findByUsername(username).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"không tìm thấy người dùng"));
        return account.getId();
        
    }

    @Override
    public String getToolName() {
        return "search_user_orders";
    }

    @Override
    public Map<String, Object> getToolDefinition() {
        return Map.of(
            "type", "function",
            "function", Map.of(
                "name", getToolName(),
                "description", "Search or check the status of the user's current orders or past purchase history.",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "order_id", Map.of(
                            "type", "integer",
                            "description", "Specific order code or ticket ID if the user mentions one (id is a long type)"
                        ),
                        "movie_title", Map.of(
                            "type", "string",
                            "description", "Filter by movie name if the user asks about a specific movie history. E.g., 'Lat Mat'"
                        ),
                        "status_filter", Map.of(
                            "type", "string",
                            "enum", List.of("SUCCESS", "PENDING"),
                            "description", "Filter by order status. CRITICAL: Omit this field entirely if the user does not specify a status pass SUCCESS. Never pass an empty string."
                        )
                    )
                    // KHÔNG CÓ TRƯỜNG REQUIRED: AI hoàn toàn tự do nhặt tham số tùy theo độ mông lung của câu hỏi
                )
            )
        );
    }

    @Override
    public String execute(String argumentsJson) {
        Long accountId = getAccountIdFromToken();
        System.out.println("==> Khởi chạy Tra Cứu Đơn Hàng cho Account ID: " + accountId);

        try {
            JsonNode rootNode = objectMapper.readTree(argumentsJson);

            
            if (rootNode.has("order_id") && !rootNode.get("order_id").asText().isEmpty()) {
                String orderId = rootNode.get("order_id").asText();
                System.out.println("-> Tra cứu đích danh mã đơn: " + orderId);
                
                Order order =orderRepository.findByIdAndAccount_Id(Long.parseLong(orderId), accountId).orElseThrow(()->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"không tìm thấy đơn hàng"));
            Map<String, Object> singleResult = Map.of(
                "status", "success",
                "type", "single",
                "data", Map.of(
                    "order_Id", order.getId(),
                    "time", order.getCreatedAt().toString(),
                    "movieTitle", order.getShowTime().getMovie().getTitle(),
                    "status", order.getStatus(),
                    "content", order.getSeatIdsJson()
                )
            );
            return objectMapper.writeValueAsString(singleResult);
            }
            

            // Tình huống 2: Người dùng tìm kiếm mông lung (Tìm kiếm động dựa trên bộ lọc)
            

           
            Specification<Order> spec=buildSearchArgs(rootNode,accountId);
            List<Order> orders= orderRepository.findAll(spec);

            List<Map<String, Object>> resultList = orders.stream().map(s -> Map.<String, Object>of(
    "order_Id", s.getId(),
    "time", s.getCreatedAt().toString(),
    "movieTitle",s.getShowTime().getMovie().getTitle(),
    "status",s.getStatus(),
    "seats",s.getSeatIdsJson()
   
)).collect(Collectors.toList());

            Map<String, Object> finalResult = Map.of(
                "status", "success",
                "count", resultList.size(),
                "results", resultList
            );

            return objectMapper.writeValueAsString(finalResult);
        } catch (Exception e) {
            String errorMessage;
            if (e instanceof JsonProcessingException) {
                errorMessage = "Dữ liệu đầu vào không hợp lệ. Vui lòng kiểm tra lại định dạng JSON.";
            } else if (e instanceof NumberFormatException) {
                errorMessage = "Giá trị order_id không hợp lệ. Vui lòng gửi một số nguyên hợp lệ.";
            } else if (e instanceof ResponseStatusException) {
                errorMessage = e.getMessage();
            } else {
                errorMessage = "Lỗi nội bộ khi tra cứu đơn hàng. Vui lòng thử lại sau.";
            }

            System.err.println("[OrderTicketSearchTool] Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.err);

            try {
                return objectMapper.writeValueAsString(Map.of(
                        "status", "error",
                        "message", errorMessage
                ));
            } catch (JsonProcessingException jsonEx) {
                return "{\"status\": \"error\", \"message\": \"Lỗi xử lý phản hồi lỗi nội bộ.\"}";
            }
        }
    }

    private Specification<Order> buildSearchArgs(JsonNode rootNode,Long accountID)
    {
        Specification<Order> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        spec.and((root, query, criteriaBuilder) -> {return criteriaBuilder.equal(root.get("account_id"),accountID);});
        String movieTitle = rootNode.has("movie_title") ? rootNode.get("movie_title").asText() : "";
            String statusFilter = rootNode.has("status_filter") ? rootNode.get("status_filter").asText() : "";
        if(!movieTitle.isBlank())
        {
            spec.and((root, query, criteriaBuilder)->
        {
           Join<Object, Object> showtimeJoin = root.join("showtime");
            Join<Object, Object> movieJoin = showtimeJoin.join("movie");
            
            // Tìm kiếm tương đối LIKE %tên_phim%
            return criteriaBuilder.like(movieJoin.get("title"), "%" + movieTitle + "%");
        });
        }
         if(!statusFilter.isBlank())
         {
            spec.and((root, query, criteriaBuilder)->
        {
            return criteriaBuilder.equal(root.get("status"),statusFilter);
        });
         }
         
        return spec;
    }
}