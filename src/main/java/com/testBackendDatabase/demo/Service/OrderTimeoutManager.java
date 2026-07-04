package com.testBackendDatabase.demo.Service;

import com.testBackendDatabase.demo.Repository.OrderRepository;
import com.testBackendDatabase.demo.Repository.TicketHoldingRepository;

import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class OrderTimeoutManager {

    private final OrderRepository orderRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    private final TicketHoldingRepository ticketHoldingRepository;
    public OrderTimeoutManager(OrderRepository orderRepository, SimpMessagingTemplate messagingTemplate,TicketHoldingRepository ticketHoldingRepository) {
        this.orderRepository = orderRepository;
        this.messagingTemplate = messagingTemplate;
        this.ticketHoldingRepository=ticketHoldingRepository;
    }
    @SuppressWarnings("null")
    public void scheduleOrderTimeout(@NonNull Long orderId, @NonNull String username) {
        executorService.schedule(() -> {
            try {
                // Đọc lại trạng thái mới nhất của đơn hàng từ DB sau 10 phút
                var orderOpt = orderRepository.findById(orderId);
                if (orderOpt.isPresent()) {
                    var order = orderOpt.get();
                    
                    // Nếu sau 10 phút mà trạng thái vẫn là PENDING -> Tiến hành hủy
                    if ("PENDING".equals(order.getStatus())) {
                        order.setStatus("FAILED");
                        orderRepository.save(order);
                        ticketHoldingRepository.deleteByOrderId(orderId);
                        // 1. Thông báo cho Client qua Socket rằng đơn hàng đã bị hủy do hết hạn
                        messagingTemplate.convertAndSendToUser(
                                username, 
                                "/queue/payment-status", 
                                Map.of("status", "TIMEOUT", "message", "Đơn hàng đã hết hạn thanh toán (10 phút)!")
                        );
                        
                        System.out.println("Đơn hàng #" + orderId + " đã bị tự động hủy do quá hạn 10 phút.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.MINUTES); // <--- Thiết lập thời gian chờ 10 phút tại đây
    }
}