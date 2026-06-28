package com.testBackendDatabase.demo.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketBookingRequest {
    
    @NotNull(message = "showTime id null")
    private Long showtimeId;

   @NotNull(message = "seats id null")
    private List<Long> seatIds;
    
    
}
