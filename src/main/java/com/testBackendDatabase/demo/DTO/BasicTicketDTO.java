package com.testBackendDatabase.demo.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasicTicketDTO {
    private String ticketCode;   // UUID
    private String customerName;
    private String movieTitle;
    private LocalDateTime startTime;
    private String roomName;
    private String seatName;     // VD: A1, B2
    private String seatType;     // VIP, NORMAL
    private Double price;
    private LocalDateTime bookingTime;
    private boolean used; // ghi xác nhận hoặc chưa xác nhận cho vé
    private LocalDateTime usedAt; //thời điểm nhân viên quét vé
}