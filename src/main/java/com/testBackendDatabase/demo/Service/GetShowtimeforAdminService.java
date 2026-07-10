package com.testBackendDatabase.demo.Service;

import com.testBackendDatabase.demo.DTO.AdminShowTimeDTO;
import com.testBackendDatabase.demo.DTO.CinemaDTO;
import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetShowtimeforAdminService {
    private final ShowTimeRepository showTimeRepository;

    public GetShowtimeforAdminService(ShowTimeRepository showTimeRepository) {
        this.showTimeRepository = showTimeRepository;
    }
    public List<AdminShowTimeDTO> getAllShowTime() {
        return showTimeRepository.findActiveShowTimes(LocalDateTime.now())
                .stream()
                .map(c -> AdminShowTimeDTO.builder()
                        .id(c.getId())
                        .cinemaName(c.getShowRoom().getCinema().getName())
                        .movieName(c.getMovie().getTitle())
                        .address(c.getShowRoom().getCinema().getAddress())
                        .startTime(c.getStartTime())
                        .endTime(c.getEndTime())      // 👈 thêm dòng này
                        .price(c.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

}
