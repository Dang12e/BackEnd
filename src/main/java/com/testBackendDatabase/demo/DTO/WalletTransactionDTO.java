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
public class WalletTransactionDTO {

    private Double amount;
    
    private String type; 
    
    private String description; 

    private LocalDateTime transactionTime;

    
}
