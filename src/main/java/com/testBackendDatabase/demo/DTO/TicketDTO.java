package com.testBackendDatabase.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {
    // Thông tin vé
    private Long id;
    private String ticketCode;
    private String qrCodeBase64;

    // Thông tin khách hàng
    private String customerName;

    // Thông tin suất chiếu & Phim
    private String movieTitle;
    private LocalDateTime startTime;
    private String roomName;

    // Chi tiết ghế
    private String seatName;
    private String seatType;
    private Double price;

    // Trạng thái & Thời gian
    private LocalDateTime bookingTime;

    // 👇 THÊM 2 DÒNG NÀY
    private boolean used;
    private LocalDateTime usedAt;
}