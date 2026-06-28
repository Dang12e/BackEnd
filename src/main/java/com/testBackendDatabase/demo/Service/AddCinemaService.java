package com.testBackendDatabase.demo.Service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.testBackendDatabase.demo.DTO.CinemaDTO;
import com.testBackendDatabase.demo.Repository.CinemaRepository;
import com.testBackendDatabase.demo.Request.AddCinemaRequest;
import com.testBackendDatabase.demo.model.Cinema;

@Service
public class AddCinemaService {

    private final CinemaRepository cinemaRepository;

  AddCinemaService(CinemaRepository cinemaRepository) {
    this.cinemaRepository = cinemaRepository;
  }

    @Transactional
public CinemaDTO addCinemaDTO(AddCinemaRequest request) {
    // 1. Chuyển từ Request sang Entity
    Cinema cinema = Objects.requireNonNull(Cinema.builder()
            .name(request.getName())
            .address(request.getAddress())
            .build());
            
    // 2. Lưu vào DB
    Cinema savedCinema = cinemaRepository.save(cinema);
    
    // 3. Chuyển từ Entity sang DTO để trả về
    // Nếu CinemaDTO cũng có @Builder, bạn có thể viết như sau:
    return CinemaDTO.builder()
            .id(savedCinema.getId())
            .name(savedCinema.getName())
            .address(savedCinema.getAddress())
            .build();
}

    
}
