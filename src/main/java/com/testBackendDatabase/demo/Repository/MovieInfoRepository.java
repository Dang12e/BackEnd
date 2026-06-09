package com.testBackendDatabase.demo.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.testBackendDatabase.demo.model.MovieInfo;


@Repository
public interface MovieInfoRepository extends JpaRepository<MovieInfo,Long> {

    Page<MovieInfo> findAll(Pageable pageable);

   
    Page<MovieInfo> findByTitleContaining(String title,Pageable pageable);
}
