package com.testBackendDatabase.demo.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import com.testBackendDatabase.demo.model.MovieInfo;


public interface MovieInfoRepository extends JpaRepository<MovieInfo,Long> {

    @NonNull
    Page<MovieInfo> findAll(@NonNull Pageable pageable);

   
    Page<MovieInfo> findByTitleContaining(String title,Pageable pageable);
}
