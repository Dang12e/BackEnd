package com.testBackendDatabase.demo.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.DTO.SeatDTO;
import com.testBackendDatabase.demo.Service.SeatViewService;




@RestController
@RequestMapping("/api/feature")
public class SeatViewController {
    private SeatViewService seatViewService;

    SeatViewController(SeatViewService seatViewService)
    {
     this.seatViewService= seatViewService;
    }

    @GetMapping("/getSeatView")
    public ResponseEntity<List<SeatDTO>> getSeatView(@RequestParam("showTimeID") @NonNull Long showTimeID)
    {
        List<SeatDTO> seatDTOs= seatViewService.getSeatView(showTimeID);
        return ResponseEntity.ok(seatDTOs);
    }
    
    
}
