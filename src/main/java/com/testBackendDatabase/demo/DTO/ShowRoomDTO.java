package com.testBackendDatabase.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowRoomDTO {
    private Long id;

    
    private String roomName;

    private int capacity;
}
