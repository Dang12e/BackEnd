package com.testBackendDatabase.demo.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.DTO.ShowRoomDTO;
import com.testBackendDatabase.demo.Repository.CinemaRepository;
import com.testBackendDatabase.demo.Repository.SeatRepository;
import com.testBackendDatabase.demo.Repository.ShowRoomRepository;
import com.testBackendDatabase.demo.Request.AddShowRoomRequest;
import com.testBackendDatabase.demo.model.Cinema;
import com.testBackendDatabase.demo.model.Seat;
import com.testBackendDatabase.demo.model.ShowRoom;

@Service
public class AddShowRoomService {
    @Autowired
    private ShowRoomRepository showRoomRepository;
    @Autowired 
    private SeatRepository seatRepository;
    @Autowired
    private CinemaRepository cinemaRepository;

    @Transactional
    public ShowRoomDTO addShowRoom(AddShowRoomRequest request)
    {
        Cinema cinema= cinemaRepository.findById(request.getCinemaID()).orElseThrow(
            ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Rạp chiếu không tồn tại!")
        );
        ShowRoom showRoom= ShowRoom.builder().roomName(request.getRoomName()).cinema(cinema).capacity(100).build();
        ShowRoom savedRoom= showRoomRepository.save(showRoom);

        List<Seat> seats = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
        int rowIndex = i / 10; // Hàng: 0, 1, 2...
        int colIndex = i % 10; // Cột: 0, 1, 2...
        
        // Chuyển rowIndex thành ký tự (0 -> A, 1 -> B, ...)
        char rowLetter = (char) ('A' + rowIndex); 
        // Tạo tên ghế: A0, A1, A2...
        String seatName = rowLetter + String.valueOf(colIndex);
        String type = (rowIndex < 2) ? "VIP" : "NORMAL";

        Seat seat = Seat.builder()
                .rowNum(rowIndex)       // Lưu kiểu int
                .colNum(colIndex)       // Lưu kiểu int
                .name(seatName)      // Tên ghế kiểu String (A0, A1...)
                .type(type)
                .basePrice(50.0)
                .showRoom(savedRoom)
                .build();
        
        seats.add(seat);
        
    }
    seatRepository.saveAll(seats);
    return ShowRoomDTO.builder()
            .id(savedRoom.getId())
            .roomName(savedRoom.getRoomName())
            .capacity(savedRoom.getCapacity())
            .build();
     
}
}
