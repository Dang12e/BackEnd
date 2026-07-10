package com.testBackendDatabase.demo.Chatbot.Tools;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testBackendDatabase.demo.Chatbot.ChatbotTool;
import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.TicketRepository;
import com.testBackendDatabase.demo.model.Account;
import com.testBackendDatabase.demo.model.ShowTime;
import com.testBackendDatabase.demo.model.Ticket;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class TicketSearchTool implements ChatbotTool {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AccountRepository accountRepository;
    private final TicketRepository ticketRepository;


    TicketSearchTool(AccountRepository accountRepository,TicketRepository ticketRepository) {
        this.accountRepository = accountRepository;
        this.ticketRepository= ticketRepository;
    }

    

    @Override
    public String getToolName() {
        return "TicketSearchTool";
    }

    @Override
    public Map<String, Object> getToolDefinition() {

        return Map.of(
            "type", "function",
            "function", Map.of(
                "name", getToolName(),
                "description", "Search for ticket or check ticket status ",
                "parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "ticket_id", Map.of(
                            "type", "long",
                            "description", "Specific ticket ID if the user mentions one (id is a long type)"
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
                            "type", "string",
                            "description", "Theater name or city mentioned. E.g., 'Hanoi', 'CGV Ba Dinh'."
                        ),
                        "seat_name", Map.of(
                            "type", "string",
                            "description", "name of seat that user mention, only pass one if there is more than one seat name, e.g. 'A1','J9'"
                        )
                    ),
                    "required", java.util.List.of("search_type") // Chỉ bắt buộc AI phân loại luồng chính, các tham số khác có hay không cũng được!
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

            
            if (rootNode.has("ticket_id") && !rootNode.get("ticket_id").asText().isEmpty()) {
                String ticketID = rootNode.get("ticket_id").asText();
                System.out.println("-> Tra cứu đích danh mã vé: " + ticketID);
                
                Ticket ticket =ticketRepository.findByIdAndAccount_Id(Long.parseLong(ticketID), accountId).orElseThrow(()->
            new ResponseStatusException(HttpStatus.NOT_FOUND,"không tìm thấy đơn hàng"));
            Map<String, Object> singleResult = Map.of(
                "status", "success",
                "type", "single",
                "data", Map.of(
                    "ticket_Id", ticket.getId(),
                    "Booking_time", ticket.getBookingTime().toString(),
                    "movieTitle", ticket.getShowTime().getMovie().getTitle(),
                    "usage_status", ticket.isUsed(),
                    "seat_name",ticket.getSeat().getName()
                )
            );
            return objectMapper.writeValueAsString(singleResult);
            }
            

            // Tình huống 2: Người dùng tìm kiếm mông lung (Tìm kiếm động dựa trên bộ lọc)
            

           
            Specification<Ticket> spec=buildSearchArgs(rootNode,accountId);
            List<Ticket> orders= ticketRepository.findAll(spec);

            List<Map<String, Object>> resultList = orders.stream().map(t -> Map.<String, Object>of(
    "ticket_Id", t.getId(),
                    "Booking_time", t.getBookingTime().toString(),
                    "movieTitle", t.getShowTime().getMovie().getTitle(),
                    "usage_status", t.isUsed(),
                    "cinema",t.getShowTime().getShowRoom().getCinema().getName(),
                    "seatname",t.getSeat().getName()
   
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

    private Long getAccountIdFromToken() {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        Account account= accountRepository.findByUsername(username).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"không tìm thấy người dùng"));
        return account.getId();
        
    }
    private Specification<Ticket> buildSearchArgs(JsonNode rootNode,Long accountID)
    {
        
        Specification<Ticket> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("account").get("id"), accountID));

        Join<Ticket, Object> showTimeJoin = null;
        Join<Object, Object> movieJoin = null;
        Join<Object,Object> showRoomJoin=null;

    if (Long.class != query.getResultType()) {
    // 1. Fetch bảng trung gian showTime và giữ lại đối tượng Fetch
    Fetch<Ticket, Object> showTimeFetch = root.fetch("showTime", JoinType.INNER);
    Fetch<Object,Object> showRoomFetch= showTimeFetch.fetch("showRoom",JoinType.INNER);
    // 2. Từ showTimeFetch, rẽ nhánh ra fetch đồng thời cả movie lẫn showRoom (hoặc room)
    showTimeFetch.fetch("movie", JoinType.INNER);
    showRoomFetch.fetch("cinema",JoinType.INNER);
    root.fetch("seat",JoinType.INNER);
    
    // 3. Khởi tạo cấu trúc Join song song phục vụ cho mệnh đề WHERE (Cách 1 ở câu trước)
    showTimeJoin = root.join("showTime", JoinType.INNER);
    showRoomJoin=showTimeJoin.join("showRoom",JoinType.INNER);
    movieJoin = showTimeJoin.join("movie", JoinType.INNER);
    }
     else {
            // Câu lệnh COUNT (Phân trang): Chỉ cần join để lọc, KHÔNG fetch
            showTimeJoin = root.join("showTime", JoinType.INNER);
            movieJoin = showTimeJoin.join("movie", JoinType.INNER);
        }

            if (rootNode.has("keyword") && !rootNode.get("keyword").asText().isEmpty()) {
                    String keyword = rootNode.get("keyword").asText();
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(movieJoin.get("title")), 
                "%" + keyword.toLowerCase() + "%"
            ));
                }

                // 2. Điều kiện TÊN RẠP (Location) - Cần Join bảng Showtime với bảng Theater
                if (rootNode.has("location") && !rootNode.get("location").asText().isEmpty() && showRoomJoin!=null) {
                    String location = rootNode.get("location").asText();
                    Join<Object,Object> cinemaJoin= showRoomJoin.join("cinema",JoinType.INNER);
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(cinemaJoin.get("address")),"%"+location+"%"));
                }

                // 3. Điều kiện THỜI GIAN (Time Frame)
                if (rootNode.has("time_frame") && !rootNode.get("time_frame").asText().isEmpty()) {
                    String timeFrame = rootNode.get("time_frame").asText();
                    
                    if (timeFrame.equalsIgnoreCase("tonight") || timeFrame.contains("hôm nay")) {
                        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
                        LocalDateTime endOfToday = LocalDate.now().atTime(23, 59, 59);
                        
                        // Lọc các suất chiếu nằm trong ngày hôm nay
                        predicates.add(criteriaBuilder.between(root.get("bookingTime"), startOfToday, endOfToday));
                    }
                }
                if(rootNode.has("seat_name") && !rootNode.get("seat_name").asText().isEmpty())
                {
                  String seat_name=rootNode.get("seat_name").asText();
                  Join<Object,Object> seatJoin =root.join("seat",JoinType.INNER);
                  predicates.add(criteriaBuilder.equal(seatJoin.get("name"), seat_name));
                }

                // Gộp chung tất cả các điều kiện lại bằng phép toán AND
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };
            return spec;
    }
    
}
