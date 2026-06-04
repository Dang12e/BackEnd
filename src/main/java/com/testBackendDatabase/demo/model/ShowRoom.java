package com.testBackendDatabase.demo.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "showrooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    private int capacity;
  

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    @ToString.Exclude // Tránh lỗi vòng lặp vô tận khi in log
    @EqualsAndHashCode.Exclude
    private Cinema cinema;

    @OneToMany(mappedBy = "showRoom",cascade =CascadeType.ALL,orphanRemoval = true )
    @ToString.Exclude // Quan trọng: Tránh lỗi StackOverflow do vòng lặp toString
    @EqualsAndHashCode.Exclude // Tránh lỗi khi so sánh 2 object có quan hệ 2 chiều
    private List<Seat> seats;

    
    
}
