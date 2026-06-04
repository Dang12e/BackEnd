package com.testBackendDatabase.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startTime; 

    // Bạn nên thêm endTime để kiểm soát việc quét vé QR đúng giờ
    private LocalDateTime endTime; 

    private double price; 
    
    @Builder.Default // Giúp giá trị mặc định hoạt động khi dùng @Builder
    private Boolean isFull = false;
    @Builder.Default
    private Boolean isActive=true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MovieInfo movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ShowRoom showRoom;
    
    // Một suất chiếu có nhiều vé
    @OneToMany(mappedBy = "showTime", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Ticket> tickets;
}
