package com.testBackendDatabase.demo.Request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddMovieRequest {
    
    @NotBlank
    private String title;
    
    
    @NotBlank
    private String description;

    
    @NotBlank
    private String genre;

    
    @NotBlank
    private String rating;

    
    @NotNull
    private LocalDate releaseDate;

    
    @NotNull
    private Integer duration;
}
