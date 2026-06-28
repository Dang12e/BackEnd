package com.testBackendDatabase.demo.Request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "showRoomID null")
    private Long movieID;
    @NotNull(message = "showRoomID null")
    private Long showRoomID;
    
}
