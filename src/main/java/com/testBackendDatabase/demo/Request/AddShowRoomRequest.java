package com.testBackendDatabase.demo.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddShowRoomRequest {
    @NotNull(message = "ID rạp không được để trống")
    private String roomName;
    private Long cinemaID;


}
