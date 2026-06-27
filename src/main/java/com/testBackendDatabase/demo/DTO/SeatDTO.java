package com.testBackendDatabase.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {
     private Long id;

    
    private int row; 

    
    private int col; 

    private String type; // Ví dụ: "NORMAL", "VIP", "COUPLE"
    
    private Double basePrice;

    private String status;
}
