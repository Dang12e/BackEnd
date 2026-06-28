package com.testBackendDatabase.demo.Controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
    @GetMapping("/getMoviesWithPage")
    public Page<MovieDTO> getMoviesWithPage(@RequestParam int page) {
        return movieInfoService.getAllMovieWithPage(page);
    }

    @GetMapping("/getSearchResultWithPage")
    public Page<MovieDTO> getMethodName(@RequestParam int page,@RequestParam String key) {
        return movieInfoService.getSearchMovieResultPage(page, key);
    }







}
