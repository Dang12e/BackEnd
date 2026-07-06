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
public class AdminShowTimeDTO {
    private Long id;
    private String movieName;
    private String address;
    private String cinemaName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
}
