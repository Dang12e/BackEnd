package com.testBackendDatabase.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cinemas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String address;

    // mappedBy trỏ đến tên biến 'cinema' trong class ShowRoom
    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Quan trọng: Tránh lỗi StackOverflow do vòng lặp toString
    @EqualsAndHashCode.Exclude // Tránh lỗi khi so sánh 2 object có quan hệ 2 chiều
    private List<ShowRoom> showRooms;
}