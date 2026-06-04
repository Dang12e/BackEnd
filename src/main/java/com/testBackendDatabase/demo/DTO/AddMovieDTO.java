package com.testBackendDatabase.demo.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMovieDTO {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private String rating;
    private String image;
    private LocalDate releaseDate;
    private Integer duration;
}
