package com.testBackendDatabase.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mã định danh duy nhất để tạo mã QR (ví dụ: "A1B2-C3D4-E5F6")
   @Column(unique = true, nullable = false, length = 100) 
    private String ticketCode;

    // Chuỗi Base64 rất dài của ảnh QR - ĐỂ LONGTEXT Ở ĐÂY VÀ BỎ UNIQUE ĐI
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String qrCode;
    // Trạng thái quét vé
    @Builder.Default
    private boolean isUsed = false;

    private LocalDateTime usedAt; // Thời điểm quét vé

    private Double price;
    private LocalDateTime bookingTime;
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ShowTime showTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Seat seat;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="owner_id",nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Account account;
    
}
