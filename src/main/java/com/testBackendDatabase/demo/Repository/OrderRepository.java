package com.testBackendDatabase.demo.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.testBackendDatabase.demo.model.Order;

import jakarta.persistence.LockModeType;


public interface OrderRepository extends JpaRepository<Order, Long> {
@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM orders o " +
                   "WHERE o.show_time_id = :showtimeId " +
                   "AND o.status = 'PENDING' " +
                   "AND REGEXP_LIKE(o.seat_ids_json, :seatIdsPattern)", nativeQuery = true)
    boolean existsPendingSeats(@Param("showtimeId") Long showtimeId, @Param("seatIdsPattern") String seatIdsPattern);
    
   @Modifying
   @Transactional
    @Query("UPDATE Order o SET o.status = 'FAILED' WHERE o.status = 'PENDING' AND o.createdAt < :timeLimit")
    int updateExpiredOrdersToFailed(@Param("timeLimit") LocalDateTime timeLimit);

    Page<Order> findByAccount(Long id,Pageable pageable);

    @Query("SELECT o FROM Order o " +
           "JOIN FETCH o.account " +
           "JOIN FETCH o.showTime " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithAccountAndShowTime(@Param("id") Long id);

    @Query("SELECT o FROM Order o " +
           "JOIN FETCH o.account a " +
           "JOIN FETCH o.showTime " +
           "WHERE a.id = :accountId")
    @EntityGraph(attributePaths = {"account", "showTime","showTime.movie"})
    Page<Order> findByAccount_id(Long accountId, Pageable pageable);
    
}
