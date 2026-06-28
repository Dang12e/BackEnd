package com.testBackendDatabase.demo.JanitorGuy;


import com.testBackendDatabase.demo.Repository.OrderRepository;
import com.testBackendDatabase.demo.Repository.TicketHoldingRepository;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DatabaseCleanupRunner implements CommandLineRunner {


    private final OrderRepository orderRepository;
    private final TicketHoldingRepository ticketHoldingRepository;

    // Inject Repository vào thông qua Constructor
    public DatabaseCleanupRunner(OrderRepository orderRepository, TicketHoldingRepository ticketHoldingRepository) {
        this.orderRepository = orderRepository;
        this.ticketHoldingRepository=ticketHoldingRepository;
    }

    @Override
    public void run(String... args) throws Exception{
        System.out.println("=== BẮT ĐẦU CHẠY LỆNH DỌN DẸP DATABASE BAN ĐẦU ===");
        
        try {
            LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(15);
              orderRepository.updateExpiredOrdersToFailed(timeLimit);
              ticketHoldingRepository.deleteExpiredHoldings(LocalDateTime.now());
            
            
        } catch (Exception e) {
            System.err.println("Xảy ra lỗi khi dọn dẹp database ban đầu: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== KẾT THÚC TIẾN TRÌNH DỌN DẸP ===");
    }
}
