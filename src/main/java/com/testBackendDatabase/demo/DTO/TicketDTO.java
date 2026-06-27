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
    private String ticketCode;   // Mã định danh duy nhất (UUID) để quét QR
    private String qrCodeBase64; // Dữ liệu hình ảnh QR

    // Thông tin khách hàng
    private String customerName;

    // Thông tin suất chiếu & Phim
    private String movieTitle;
    private LocalDateTime startTime;
    private String roomName;

    // Chi tiết ghế
    private String seatName;     // VD: A1, B2
    private String seatType;     // VIP, NORMAL
    private Double price;        // Giá cuối cùng của vé này

    // Trạng thái & Thời gian
    private LocalDateTime bookingTime;
    
}
