package com.testBackendDatabase.demo.Controller;

import com.testBackendDatabase.demo.CloudinaryConfig.CloudinaryService;
import com.testBackendDatabase.demo.DTO.CinemaDTO;
import com.testBackendDatabase.demo.DTO.ShowRoomDTO;
import com.testBackendDatabase.demo.Request.AddCinemaRequest;
import com.testBackendDatabase.demo.Request.AddShowRoomRequest;
import com.testBackendDatabase.demo.Service.AddCinemaService;
import com.testBackendDatabase.demo.Service.AddShowRoomService;

import jakarta.validation.Valid;

import com.testBackendDatabase.demo.Service.CinemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final CloudinaryService cloudinaryService;

    private final AddCinemaService addCinemaService;
    private final AddShowRoomService addShowRoomService;
    private final CinemaService cinemaService;

    MediaController(CloudinaryService cloudinaryService, AddCinemaService addCinemaService, AddShowRoomService addShowRoomService,
        CinemaService cinemaService
    ) {
        this.cloudinaryService = cloudinaryService;
        this.addCinemaService = addCinemaService;
        this.addShowRoomService = addShowRoomService;
        this.cinemaService=cinemaService;
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
        
        
        //TODO: process POST request
        if(request.getName()==null || request.getName().trim().isEmpty()||
                request.getAddress()==null||request.getAddress().trim().isEmpty()){
            throw new IllegalArgumentException("Tên rạp và địa chỉ rạp không được để trống");

        }
        CinemaDTO cinemaDTO =addCinemaService.addCinemaDTO(request);
        return ResponseEntity.ok(cinemaDTO);
    }

   
    @PostMapping("addShowRoom")
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
    @GetMapping("/cinemas")
    public ResponseEntity<List<CinemaDTO>> getAllCinemas() {
        List<CinemaDTO> cinemas = addCinemaService.getAllCinemas();
        return ResponseEntity.ok(cinemas);
    }
    @PostMapping("/getShowRooms")
    public ResponseEntity<List<ShowRoomDTO>> getShowRooms() {
        List<ShowRoomDTO> showRoomDTOs = addShowRoomService.getAllShowRooms();
        return ResponseEntity.ok(showRoomDTOs);
    }


}