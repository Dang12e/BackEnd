package com.testBackendDatabase.demo.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddShowRoomRequest {
    
    private String roomName;
    private Long cinemaID;


}
