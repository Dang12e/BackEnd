package com.testBackendDatabase.demo.model;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "ticket_holdings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_showtime_seat", 
            columnNames = {"showtime_id", "seat_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt; // Thời điểm hết hạn giữ ghế (Ví dụ: thời điểm tạo + 10 phút)

    /**
     * Helper method kiểm tra xem bản ghi giữ ghế này đã quá hạn thanh toán chưa
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}