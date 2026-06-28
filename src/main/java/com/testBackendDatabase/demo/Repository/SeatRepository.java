package com.testBackendDatabase.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.testBackendDatabase.demo.model.Seat;

public interface SeatRepository extends JpaRepository<Seat,Long> {

    //Optional<Seat> findByShowTime(ShowTime showTime);
   
    
}
