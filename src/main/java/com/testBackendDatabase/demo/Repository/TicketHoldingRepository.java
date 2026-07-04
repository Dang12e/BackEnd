package com.testBackendDatabase.demo.Repository; // Thay đổi package phù hợp với dự án của bạn

import com.testBackendDatabase.demo.model.TicketHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface TicketHoldingRepository extends JpaRepository<TicketHolding, Long> {

    
    void deleteByOrderId(Long orderId);

    
    @Modifying
    @Transactional
    @Query("DELETE FROM TicketHolding th WHERE th.expiresAt < :now")
    void deleteExpiredHoldings(@Param("now") LocalDateTime now);
}