package com.testBackendDatabase.demo.Service;

import com.testBackendDatabase.demo.DTO.CinemaDTO;
import com.testBackendDatabase.demo.Repository.CinemaRepository;
import com.testBackendDatabase.demo.model.Cinema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CinemaService {

    @Autowired
    private CinemaRepository cinemaRepository;

    public List<CinemaDTO> getAllCinemas() {
        return cinemaRepository.findAll()
                .stream()
                .map(cinema -> CinemaDTO.builder()
                        .id(cinema.getId())
                        .name(cinema.getName())
                        .address(cinema.getAddress())
                        .build())
                .collect(Collectors.toList());
    }
}