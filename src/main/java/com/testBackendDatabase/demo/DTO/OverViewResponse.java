package com.testBackendDatabase.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverViewResponse {

    private Double totalRevenue;

    private Long totalTickets;

    private Long activeShowTimes;
}
