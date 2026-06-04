package com.testBackendDatabase.demo.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.CloudinaryConfig.CloudinaryService;
import com.testBackendDatabase.demo.DTO.AddMovieDTO;
import com.testBackendDatabase.demo.DTO.MovieDTO;
import com.testBackendDatabase.demo.DTO.MovieInfoDTO;
import com.testBackendDatabase.demo.DTO.ShowTimeDTO;
import com.testBackendDatabase.demo.Repository.MovieInfoRepository;
import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import com.testBackendDatabase.demo.Request.AddMovieRequest;
import com.testBackendDatabase.demo.model.MovieInfo;



@Service
public class MovieInfoService {

@Autowired
private MovieInfoRepository movieInfoRepository;
@Autowired
private ShowTimeRepository showTimeRepository;
@Autowired
private CloudinaryService cloudinaryService;

@Transactional(readOnly = true) // Tối ưu hiệu năng vì chỉ đọc dữ liệu
    public List<ShowTimeDTO> getShowTimesByMovie(Long movieId) {
        // 1. Logic kiểm tra phim tồn tại nằm ở đây
        MovieInfo movie = movieInfoRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Phim không tồn tại!"));
        
        // 2. Lấy dữ liệu và map sang DTO
        return showTimeRepository.findActiveByMovie(movie,LocalDateTime.now()).stream()
                .map(st -> {
                    ShowTimeDTO dto = new ShowTimeDTO();
                    dto.setId(st.getId());
                    dto.setStartTime(st.getStartTime());
                    dto.setAddress(st.getShowRoom().getCinema().getAddress());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public AddMovieDTO addMovieInfo(AddMovieRequest request,MultipartFile file)
    {
        String image=null;
        try{
       image=cloudinaryService.uploadFile(file);
        }
        catch(IOException e)
        {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File ảnh không hợp lệ hoặc lỗi đường truyền!");
        }
        MovieInfo movie =MovieInfo.builder().description(request.getDescription())
        .duration(request.getDuration())
        .genre(request.getGenre())
        .rating(request.getRating())
        .releaseDate(request.getReleaseDate())
        .title(request.getTitle())
        .image(image)
        .build();

        

        MovieInfo movieInfo= movieInfoRepository.save(movie);
        return new AddMovieDTO(movieInfo.getId(),movieInfo.getTitle()
        ,movieInfo.getDescription(),movieInfo.getGenre(),
    movieInfo.getRating(),movieInfo.getImage(),movieInfo.getReleaseDate(),movieInfo.getDuration());
        
        
    }

    @Transactional(readOnly=true)
    public MovieInfoDTO getMovieDetail(Long movieID)
    {
       MovieInfo movieInfo= movieInfoRepository.findById(movieID).
       orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"khong tim thay phim"));
       
       return MovieInfoDTO.builder().id(movieInfo.getId()).description(movieInfo.getDescription())
       .duration(movieInfo.getDuration()).genre(movieInfo.getGenre()).image(movieInfo.getImage()).rating(movieInfo.getRating())
       .releaseDate(movieInfo.getReleaseDate()).title(movieInfo.getTitle()).build();
    }

    @Transactional(readOnly = true)
    public List<MovieDTO> getMovies()
    {
        List<MovieInfo> movies = movieInfoRepository.findAll();
        
        return movies.stream().map(
            m->{
                MovieDTO movieDTO=MovieDTO.builder()
                .id(m.getId())
                .genre(m.getGenre())
                .image(m.getImage())
                .releaseDate(m.getReleaseDate())
                .title(m.getTitle()).build();

                return movieDTO;
            }
        ).collect(Collectors.toList());

    }
    
}
