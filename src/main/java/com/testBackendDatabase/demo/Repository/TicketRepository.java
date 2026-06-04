package com.testBackendDatabase.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.testBackendDatabase.demo.model.Seat;
import com.testBackendDatabase.demo.model.ShowTime;
import com.testBackendDatabase.demo.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

    Ticket findByShowTime_idAndSeat_id(ShowTime showTime,Seat seat);

    boolean existsByShowTimeAndSeat(ShowTime showTime, Seat seat);

    @Query("SELECT t FROM Ticket t " +
       "JOIN FETCH t.account a " + // Đặt alias 'a' cho account
       "JOIN FETCH t.showTime st " +
       "JOIN FETCH st.movie " +
       "JOIN FETCH st.showRoom " +
       "JOIN FETCH t.seat " +
       "WHERE a.id = :accountId") // Sử dụng alias.id
List<Ticket> findAllByAccountId(@Param("accountId") Long accountId);

}
