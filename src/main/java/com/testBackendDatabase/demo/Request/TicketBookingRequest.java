package com.testBackendDatabase.demo.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketBookingRequest {
    
    
    private Long showtimeId;

   
    private List<Long> seatIds;
    
    
}
