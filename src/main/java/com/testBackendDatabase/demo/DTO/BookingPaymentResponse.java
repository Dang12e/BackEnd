package com.testBackendDatabase.demo.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingPaymentResponse {
    private Long orderId;
    private String paymentUrl;

}
