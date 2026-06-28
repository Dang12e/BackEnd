package com.testBackendDatabase.demo.Controller;

import com.testBackendDatabase.demo.CloudinaryConfig.*;
import com.testBackendDatabase.demo.DTO.CinemaDTO;
import com.testBackendDatabase.demo.DTO.ShowRoomDTO;
import com.testBackendDatabase.demo.Request.AddCinemaRequest;
import com.testBackendDatabase.demo.Request.AddShowRoomRequest;
import com.testBackendDatabase.demo.Service.AddCinemaService;
import com.testBackendDatabase.demo.Service.AddShowRoomService;

import com.testBackendDatabase.demo.Service.CinemaService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.testBackendDatabase.demo.DTO.ShowRoomDTO;
import com.testBackendDatabase.demo.Service.AddShowRoomService;
import java.util.List;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private CinemaService cinemaService;
    @Autowired 
    private AddCinemaService addCinemaService;
    @Autowired
    private AddShowRoomService addShowRoomService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Gọi service để upload và lấy link
            String imageUrl = cloudinaryService.uploadFile(file);
            
            // Trả về link ảnh cho client với trạng thái HTTP 200 OK
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            // Xử lý nếu có lỗi xảy ra trong quá trình upload
            return ResponseEntity.status(500).body("Upload thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/addCinema")
    public ResponseEntity<CinemaDTO> addCinema(@Valid @RequestBody AddCinemaRequest request) {
        //TODO: process POST request
        if(request.getName()==null || request.getName().trim().isEmpty()||
                request.getAddress()==null||request.getAddress().trim().isEmpty()){
            throw new IllegalArgumentException("Tên rạp và địa chỉ rạp không được để trống");

        }
        CinemaDTO cinemaDTO =addCinemaService.addCinemaDTO(request);
        return ResponseEntity.ok(cinemaDTO);
    }

    @PostMapping("/aaddShowRoom")
    public ResponseEntity<ShowRoomDTO> postMethodName(@Valid @RequestBody AddShowRoomRequest request) {
        //TODO: process POST request
        ShowRoomDTO showRoomDTO = addShowRoomService.addShowRoom(request);
        return ResponseEntity.ok(showRoomDTO);
    }

    @GetMapping("/getCinemas")
    public ResponseEntity<List<CinemaDTO>> getCinemas() {
        List<CinemaDTO> cinemas = cinemaService.getAllCinemas();
        return ResponseEntity.ok(cinemas);
    }
    @PostMapping("/getShowRooms")
    public ResponseEntity<List<ShowRoomDTO>> getShowRooms() {
        List<ShowRoomDTO> showRoomDTOs = addShowRoomService.getAllShowRooms();
        return ResponseEntity.ok(showRoomDTOs);
    }
    
    
}