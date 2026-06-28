package com.testBackendDatabase.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.testBackendDatabase.demo.DTO.CinemaDTO;
import com.testBackendDatabase.demo.Repository.CinemaRepository;
import com.testBackendDatabase.demo.Request.AddCinemaRequest;
import com.testBackendDatabase.demo.model.Cinema;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddCinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;
    public List<CinemaDTO> getAllCinemas() {
        return cinemaRepository.findAll()
                .stream()
                .map(c -> CinemaDTO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .address(c.getAddress())
                        .build())
                .collect(Collectors.toList());
    }
    @Transactional
public CinemaDTO addCinemaDTO(AddCinemaRequest request) {
    // 1. Chuyển từ Request sang Entity
    Cinema cinema = Cinema.builder()
            .name(request.getName())
            .address(request.getAddress())
            .build();
            
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
