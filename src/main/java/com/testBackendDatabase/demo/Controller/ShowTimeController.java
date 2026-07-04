package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.testBackendDatabase.demo.DTO.AddShowTimeDTO;
import com.testBackendDatabase.demo.DTO.ShowTimeDTO;
import com.testBackendDatabase.demo.Request.AddShowTimeRequest;
import com.testBackendDatabase.demo.Request.ShowTimeRequest;
import com.testBackendDatabase.demo.Service.ShowTimeService;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/feature")
public class ShowTimeController {


    private final ShowTimeService showTimeService;

    ShowTimeController(ShowTimeService showTimeService) {
        this.showTimeService = showTimeService;
    }
    @PostMapping("/getShowTime")
public ResponseEntity<List<ShowTimeDTO>> getShowTimes(@RequestBody @NonNull ShowTimeRequest request) {
    
    List<ShowTimeDTO> showTimeDTOs= showTimeService.getShowTimes(request);
    return ResponseEntity.ok(showTimeDTOs);
    
}
    @PostMapping("/addShowTime")
    public ResponseEntity<AddShowTimeDTO> postMethodName(@RequestBody @NonNull AddShowTimeRequest request) {
        AddShowTimeDTO addShowTimeDTO= showTimeService.addShowTime(request);
        return ResponseEntity.ok(addShowTimeDTO);

    }
    

    
}
