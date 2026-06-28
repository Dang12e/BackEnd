package com.testBackendDatabase.demo.Controller;

import com.testBackendDatabase.demo.CloudinaryConfig.CloudinaryService;
import com.testBackendDatabase.demo.DTO.CinemaDTO;
import com.testBackendDatabase.demo.DTO.ShowRoomDTO;
import com.testBackendDatabase.demo.Request.AddCinemaRequest;
import com.testBackendDatabase.demo.Request.AddShowRoomRequest;
import com.testBackendDatabase.demo.Service.AddCinemaService;
import com.testBackendDatabase.demo.Service.AddShowRoomService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final CloudinaryService cloudinaryService;

    private final AddCinemaService addCinemaService;
    private final AddShowRoomService addShowRoomService;

    MediaController(CloudinaryService cloudinaryService, AddCinemaService addCinemaService, AddShowRoomService addShowRoomService) {
        this.cloudinaryService = cloudinaryService;
        this.addCinemaService = addCinemaService;
        this.addShowRoomService = addShowRoomService;
    }

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
        
        
        CinemaDTO cinemaDTO =addCinemaService.addCinemaDTO(request);
        return ResponseEntity.ok(cinemaDTO);
    }

    @PostMapping("addShowRoom")
    public ResponseEntity<ShowRoomDTO> postMethodName(@Valid @RequestBody AddShowRoomRequest request) {
    
        ShowRoomDTO showRoomDTO= addShowRoomService.addShowRoom(request);
        return ResponseEntity.ok(showRoomDTO);
    }
    
    
}