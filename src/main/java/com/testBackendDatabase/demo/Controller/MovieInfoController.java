package com.testBackendDatabase.demo.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
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
import com.testBackendDatabase.demo.Service.MovieInfoService;

import jakarta.validation.Valid;

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

    @GetMapping("/getShowTimes")
    public ResponseEntity<List<ShowTimeDTO>> getShowTimes(@RequestParam @NonNull Long movieID) {
        List<ShowTimeDTO> showtimes= movieInfoService.getShowTimesByMovie(movieID);
        return ResponseEntity.ok(showtimes);
    }

    @PostMapping("/addMovieInfo")
    public ResponseEntity<AddMovieDTO> addMovieInfo(@Valid @RequestPart("data") AddMovieRequest request,@RequestPart("file") MultipartFile image) {

        AddMovieDTO movieDTO= movieInfoService.addMovieInfo(request, image);
        return ResponseEntity.ok(movieDTO);
    }

    @GetMapping("/getMovieDetail")
    public ResponseEntity<MovieInfoDTO> getMovieDetail(@RequestParam @NonNull Long movieID) {
        MovieInfoDTO movieInfoDTO= movieInfoService.getMovieDetail(movieID);
        return ResponseEntity.ok(movieInfoDTO);
    }

    @GetMapping("/getMovies")
    public ResponseEntity<List<MovieDTO>> getMovies() {
        List<MovieDTO> movieDTOs= movieInfoService.getMovies();
        return ResponseEntity.ok(movieDTOs);
    }
    @GetMapping("/getMoviesWithPage")
    public Page<MovieDTO> getMoviesWithPage(@RequestParam int page,@RequestParam int size) {
        return movieInfoService.getAllMovieWithPage(page,size);
    }
    @GetMapping("/getSearchResultWithPage")
    public Page<MovieDTO> getMethodName(@RequestParam int page,@RequestParam int size,@RequestParam String key) {
        return movieInfoService.getSearchMovieResultPage(page,size
            , key);
    }

    @GetMapping("/getMoviesForHomepage")
    public Page<MovieDTO> getMoviesForHomePage(@RequestParam int page,@RequestParam int size) {
        return movieInfoService.getMovieForHomePage(page,size);
    }
    
    
    
    
    
    
    
}
