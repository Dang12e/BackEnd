package com.testBackendDatabase.demo.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieInfoDTO {

    private Long id; 
    
    private String title;
    private String description;
    private String genre;
    private String image;
    private String rating;
    private LocalDate releaseDate;
    private Integer duration;
    
}
