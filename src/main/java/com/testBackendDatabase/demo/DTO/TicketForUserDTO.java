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
public class TicketForUserDTO {
    private String ticketCode; 
    private String customerName;
    private String movieTitle;
    private LocalDateTime startTime;
    private String roomName;
    private String seatName;     
    private String seatType;     
    private Double price;        
    private LocalDateTime bookingTime;
    private boolean used; // hiện xem vé đã được xác nhận hay chưa
    private LocalDateTime usedAt; // thời điểm nhân viên quét vé
}
