package com.testBackendDatabase.demo.Repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import com.testBackendDatabase.demo.model.MovieInfo;


public interface MovieInfoRepository extends JpaRepository<MovieInfo,Long> {

    @NonNull
    Page<MovieInfo> findAll(@NonNull Pageable pageable);

   
    Page<MovieInfo> findByTitleContaining(String title,Pageable pageable);

    @Query(value = "SELECT m.* FROM movies m " +
            "JOIN (" +
            "    SELECT st.movie_id AS movie_id " +
            "    FROM showtimes st " +
            "    JOIN tickets t ON t.showtime_id = st.id " +
            "    GROUP BY st.movie_id " +
            "    ORDER BY COUNT(t.id) DESC " +
            "    LIMIT 1" +
            ") top_movie ON top_movie.movie_id = m.id",
            nativeQuery = true)
    Optional<MovieInfo> findTopBookedMovie();
}
