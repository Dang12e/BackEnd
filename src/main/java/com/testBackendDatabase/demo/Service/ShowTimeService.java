package com.testBackendDatabase.demo.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.DTO.AddShowTimeDTO;
import com.testBackendDatabase.demo.DTO.ShowTimeDTO;
import com.testBackendDatabase.demo.Repository.MovieInfoRepository;
import com.testBackendDatabase.demo.Repository.ShowRoomRepository;
import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import com.testBackendDatabase.demo.Request.AddShowTimeRequest;
import com.testBackendDatabase.demo.Request.ShowTimeRequest;
import com.testBackendDatabase.demo.model.MovieInfo;
import com.testBackendDatabase.demo.model.ShowRoom;
import com.testBackendDatabase.demo.model.ShowTime;

@Service
public class ShowTimeService {
    
    private final ShowTimeRepository showTimeRepository;
    private final ShowRoomRepository showRoomRepository;
    private final MovieInfoRepository movieInfoRepository;

     ShowTimeService(MovieInfoRepository movieInfoRepository, ShowRoomRepository showRoomRepository, ShowTimeRepository showTimeRepository) {
          this.movieInfoRepository = movieInfoRepository;
          this.showRoomRepository = showRoomRepository;
          this.showTimeRepository = showTimeRepository;
     }

    @Transactional
    @SuppressWarnings("null")
    public AddShowTimeDTO addShowTime(@NonNull AddShowTimeRequest request)
    {
      MovieInfo movieInfo = movieInfoRepository.findById(request.getMovieID()).orElseThrow(
        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy phim!")
      );

      ShowRoom showRoom = showRoomRepository.findById(request.getShowRoomID()).orElseThrow(
        ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Không tìm thấy phòng chiếu")
      );
      ShowTime showTime= Objects.requireNonNull(ShowTime.builder().endTime(request.getEndTime()).
      startTime(request.getStartTime()).movie(movieInfo).showRoom(showRoom)
      .price(request.getPrice()).build());
      ShowTime savedShowTime=showTimeRepository.save(showTime);
      
      return AddShowTimeDTO.builder().endTime(savedShowTime.getEndTime())
      .id(savedShowTime.getId())
      .isFull(savedShowTime.getIsFull())
      .movieID(savedShowTime.getMovie().getId())
      .price(savedShowTime.getPrice())
      .showRoomID(savedShowTime.getShowRoom().getId())
      .startTime(savedShowTime.getStartTime()).build();

    }
    @Transactional(readOnly=true)
    @SuppressWarnings("null")
    public List<ShowTimeDTO> getShowTimes(@NonNull ShowTimeRequest request)
    {
        MovieInfo movie = movieInfoRepository.findById(request.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Phim không tồn tại!"));
    
    // 2. Lấy suất chiếu và map thẳng địa chỉ vào DTO
    List<ShowTimeDTO> result = showTimeRepository.findActiveByMovie(movie,LocalDateTime.now()).stream()
            .map(st -> {
                ShowTimeDTO dto = new ShowTimeDTO();
                dto.setId(st.getId());
                dto.setStartTime(st.getStartTime());
                String CinemaName=st.getShowRoom().getCinema().getName();
                dto.setCinemaName(CinemaName);
                // Lấy địa chỉ rạp: ShowTime -> Room -> Cinema -> Address
                String address = st.getShowRoom().getCinema().getAddress();
                dto.setAddress(address); // Giả định DTO của bạn có trường này
                
                return dto;
            })
            .collect(Collectors.toList());
            return result;
    
    }
    
}
