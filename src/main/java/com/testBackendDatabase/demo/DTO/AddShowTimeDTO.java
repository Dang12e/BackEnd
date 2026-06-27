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

public class AddShowTimeDTO {

    private Long id;
    private LocalDateTime startTime; 
    private LocalDateTime endTime; 
    private double price; 
    private Boolean isFull;
    private Long movieID;
    private Long showRoomID;
    
}
