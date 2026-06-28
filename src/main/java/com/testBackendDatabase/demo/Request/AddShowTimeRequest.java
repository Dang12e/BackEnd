package com.testBackendDatabase.demo.Request;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddShowTimeRequest {
    private LocalDateTime startTime; 
    private LocalDateTime endTime; 
    private double price; 
    private Long movieID;
    private Long showRoomID;
    
}
