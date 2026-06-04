package com.testBackendDatabase.demo.Repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.testBackendDatabase.demo.model.MovieInfo;
import com.testBackendDatabase.demo.model.ShowTime;



public interface ShowTimeRepository extends JpaRepository<ShowTime,Long> {

    @Query("SELECT st FROM ShowTime st " +
           "JOIN FETCH st.showRoom sr " +
           "JOIN FETCH sr.cinema c " +
           "WHERE st.movie = :movie")
    List<ShowTime> findByMovie(MovieInfo movie);
    
}