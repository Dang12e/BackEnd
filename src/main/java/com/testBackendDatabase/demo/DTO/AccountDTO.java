package com.testBackendDatabase.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private Long id;

    private String username;

    private String email;

    private String role;
    @Builder.Default
    private Double balance = 0.0;
    
}
