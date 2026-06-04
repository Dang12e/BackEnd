package com.testBackendDatabase.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testBackendDatabase.demo.model.MovieInfo;

@Repository
public interface MovieInfoRepository extends JpaRepository<MovieInfo,Long> {

    
}
