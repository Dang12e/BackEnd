package com.testBackendDatabase.demo.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.testBackendDatabase.demo.model.MovieInfo;
import com.testBackendDatabase.demo.model.ShowTime;



public interface ShowTimeRepository extends JpaRepository<ShowTime,Long> {

    @Query("SELECT st FROM ShowTime st " +
           "JOIN FETCH st.showRoom sr " +
           "JOIN FETCH sr.cinema c " +
           "WHERE st.movie = :movie " +
           "AND st.endTime > :currentTime")
    List<ShowTime> findActiveByMovie(
            @Param("movie") MovieInfo movie, 
            @Param("currentTime") LocalDateTime currentTime
    );

    // 2. Lấy chi tiết 1 suất chiếu theo ID (chỉ cho phép nếu suất chiếu đó chưa kết thúc)
    @Query("SELECT st FROM ShowTime st " +
           "JOIN FETCH st.movie " +
           "JOIN FETCH st.showRoom " +
           "WHERE st.id = :id " +
           "AND st.endTime > :currentTime")
    Optional<ShowTime> findActiveByIdWithMovieAndRoom(
            @Param("id") Long id, 
            @Param("currentTime") LocalDateTime currentTime
    );
}