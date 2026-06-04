package com.testBackendDatabase.demo.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.testBackendDatabase.demo.DTO.AddMovieDTO;
import com.testBackendDatabase.demo.DTO.MovieDTO;
import com.testBackendDatabase.demo.DTO.MovieInfoDTO;
import com.testBackendDatabase.demo.DTO.ShowTimeDTO;
import com.testBackendDatabase.demo.Request.AddMovieRequest;
import com.testBackendDatabase.demo.Request.ShowTimeRequest;
import com.testBackendDatabase.demo.Service.MovieInfoService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/api/feature")//CÁI NÀY TÔI TỰ ĐẶT AE KHÔNG THÍCH THÌ ĐẶT LẠI
public class MovieInfoController {

   
    private final MovieInfoService movieInfoService;

    MovieInfoController(MovieInfoService movieInfoService)
    {
        this.movieInfoService=movieInfoService;
    }

    @PostMapping("/getMovieDtail")
    public ResponseEntity<List<ShowTimeDTO>> getShowTimes(@RequestBody ShowTimeRequest request) {
        List<ShowTimeDTO> showtimes= movieInfoService.getShowTimesByMovie(request.getId());
        return ResponseEntity.ok(showtimes);
    }

    @PostMapping("/addMovieInfo")
    public ResponseEntity<AddMovieDTO> addMovieInfo(@Valid @RequestPart("data") AddMovieRequest request,@RequestPart("file") MultipartFile image) {

        AddMovieDTO movieDTO= movieInfoService.addMovieInfo(request, image);
        return ResponseEntity.ok(movieDTO);
    }

    @GetMapping("/getMovieDetail")
    public ResponseEntity<MovieInfoDTO> getMovieDetail(@RequestParam Long movieID) {
        MovieInfoDTO movieInfoDTO= movieInfoService.getMovieDetail(movieID);
        return ResponseEntity.ok(movieInfoDTO);
    }

    @GetMapping("/getMovies")
    public ResponseEntity<List<MovieDTO>> getMovies() {
        List<MovieDTO> movieDTOs= movieInfoService.getMovies();
        return ResponseEntity.ok(movieDTOs);
    }
    
    
    
    
    
}
