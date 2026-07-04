package com.testBackendDatabase.demo.Controller;

import com.testBackendDatabase.demo.DTO.AdminShowTimeDTO;
import com.testBackendDatabase.demo.DTO.ShowTimeDTO;
import com.testBackendDatabase.demo.Request.ShowTimeRequest;
import com.testBackendDatabase.demo.Service.GetShowtimeforAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feature")
public class GetShowTimeforAdminController {
    private final GetShowtimeforAdminService getShowtimeforAdminService;

    public GetShowTimeforAdminController(GetShowtimeforAdminService getShowtimeforAdminService) {
        this.getShowtimeforAdminService = getShowtimeforAdminService;
    }
    @GetMapping("/GetShowTimeAdmin")
    public ResponseEntity<List<AdminShowTimeDTO>> getShowTimeAdmin (){
        List<AdminShowTimeDTO> list = getShowtimeforAdminService.getAllShowTime();
        return ResponseEntity.ok(list);
    }
}
