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
public class OrderDTO {

    private String movieName;
    
    private Double totalAmount;
    
    private String status; 
    
    private LocalDateTime createdAt;
    
    private String seatIdsJson; 
    
}
