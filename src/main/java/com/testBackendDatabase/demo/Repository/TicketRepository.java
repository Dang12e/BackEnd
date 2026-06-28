package com.testBackendDatabase.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

@Query("SELECT t FROM Ticket t " +
       "JOIN FETCH t.account a " + // Đặt alias 'a' cho account
       "JOIN FETCH t.showTime st " +
       "JOIN FETCH st.movie " +
       "JOIN FETCH st.showRoom " +
       "JOIN FETCH t.seat " +
       "WHERE a.id = :accountId and t.ticketCode= :tCode"
)
Optional<Ticket> findByAccountAndTicketCodeFetchJoin(@Param("accountId") Long accountId,@Param("tCode") String tCode);

@Query("SELECT t.seat.id FROM Ticket t WHERE t.showTime.id = :showTimeId AND t.seat.id IN :seatIds")
List<Long> findBookedSeatIds(@Param("showTimeId") Long showTimeId, @Param("seatIds") List<Long> seatIds);

@EntityGraph(attributePaths = {
        "showTime",
        "showTime.movie",
        "showTime.showRoom",
        "seat"
    })
    Page<Ticket> findByAccountId(Long accountId, Pageable pageable);

    @Query("SELECT t FROM Ticket t " +           // Lấy vé
            "JOIN FETCH t.account " +              // Kèm thông tin chủ vé (tên khách)
            "JOIN FETCH t.showTime st " +          // Kèm suất chiếu
            "JOIN FETCH st.movie " +               // Kèm tên phim
            "JOIN FETCH st.showRoom " +            // Kèm tên phòng chiếu
            "JOIN FETCH t.seat " +                 // Kèm thông tin ghế (A1, B2...)
            "WHERE t.ticketCode = :ticketCode")    // Điều kiện: đúng mã QR
    Optional<Ticket> findByTicketCode(@Param("ticketCode") String ticketCode);

}
