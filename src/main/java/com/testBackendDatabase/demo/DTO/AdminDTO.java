package com.testBackendDatabase.demo.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDTO {

    private Long id;

    private String username;

    private String fullName;

    private String role;

}