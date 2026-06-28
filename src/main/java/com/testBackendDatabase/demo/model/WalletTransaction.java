package com.testBackendDatabase.demo.model;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount; // Số tiền (ví dụ: -50.0 là trừ tiền, +100.0 là nạp tiền)
    
    private String type; // Ví dụ: "PAYMENT", "TOPUP", "REFUND", "TICKET_BUY_DIRECT"
    
    private String description; // Nội dung: "Mua vé phim Batman"

    private LocalDateTime transactionTime;

    // --- 🚀 HAI TRƯỜNG MỚI ĐƯỢC BỔ SUNG TẠI ĐÂY ---
    
    @Column(name = "txn_ref", unique = true) 
    private String txnRef; // Mã giao dịch duy nhất từ VNPAY hoặc mã Đơn hàng (Ví dụ: ORD_12345)

    private String status; // Trạng thái giao dịch: "PENDING", "SUCCESS", "FAILED"

    // --------------------------------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Account account;
}