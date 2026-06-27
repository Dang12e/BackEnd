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
    
    private String type; // Ví dụ: "PAYMENT" (Thanh toán), "TOPUP" (Nạp tiền), "REFUND" (Hoàn tiền)
    
    private String description; // Nội dung: "Mua vé phim Batman"

    private LocalDateTime transactionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Account account;
}

