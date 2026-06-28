package com.testBackendDatabase.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // Thêm Builder để dễ tạo ghế hàng loạt
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "seat_row") // Tránh trùng từ khóa 'row' của SQL
    private int rowNum; 

    @Column(name = "seat_col")
    private int colNum; 

    private String type; // Ví dụ: "NORMAL", "VIP", "COUPLE"
    
    private Double basePrice;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_room_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ShowRoom showRoom;

  
}
