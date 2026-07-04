package com.testBackendDatabase.demo.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.DTO.SeatDTO;

import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import com.testBackendDatabase.demo.model.ShowTime;

@Service
public class SeatViewService {

    private final ShowTimeRepository showTimeRepository;


   SeatViewService(ShowTimeRepository showTimeRepository) {
      this.showTimeRepository = showTimeRepository;
   }

    
    @Transactional(readOnly=true)
    public List<SeatDTO> getSeatView(@NonNull Long showtimeID) {
    // 1. Tìm suất chiếu (Nên đổi thông báo thành "Suất chiếu không tồn tại")
    ShowTime showTime = showTimeRepository.findById(showtimeID)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Suất chiếu không tồn tại!"));

    // 2. Lấy danh sách ID ghế đã đặt từ danh sách vé của suất chiếu
    Set<Long> bookedSeatIds = showTime.getTickets().stream()
            .map(t -> t.getSeat().getId())
            .collect(Collectors.toSet());

    // 3. Map từ Seat sang SeatDTO
    return showTime.getShowRoom().getSeats().stream().map(s -> {
        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setId(s.getId());
        seatDTO.setCol(s.getColNum());
        seatDTO.setRow(s.getRowNum());
        seatDTO.setType(s.getType());
        
        // Kiểm tra trạng thái
        seatDTO.setStatus(bookedSeatIds.contains(s.getId()) ? "BOOKED" : "AVAILABLE");

        // Tính giá (Viết "VIP".equals để tránh NullPointerException)
        if ("VIP".equals(s.getType())) {
            seatDTO.setBasePrice(1.5d * s.getBasePrice());
        } else {
            seatDTO.setBasePrice(s.getBasePrice());
        }
        
        return seatDTO;
    }).collect(Collectors.toList());
}

}
