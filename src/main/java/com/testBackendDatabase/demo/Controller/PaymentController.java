package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.DTO.BookingPaymentResponse;
import com.testBackendDatabase.demo.Request.TicketBookingRequest;
import com.testBackendDatabase.demo.Service.TicketPaymentService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    
    private final SimpMessagingTemplate messagingTemplate;

    private final TicketPaymentService ticketPaymentService;

    public PaymentController(TicketPaymentService ticketPaymentService,SimpMessagingTemplate messagingTemplate)
    {
       this.ticketPaymentService=ticketPaymentService;
       this.messagingTemplate=messagingTemplate;
    }

    @PostMapping("/bookTickets")
    public ResponseEntity<BookingPaymentResponse> postMethodName(@RequestBody @NonNull TicketBookingRequest bookingRequest,HttpServletRequest request ) {
        BookingPaymentResponse bResponse= ticketPaymentService.generateBookingPaymentURL(bookingRequest, request);
        return ResponseEntity.ok(bResponse);
    }
    @GetMapping("/vnpay-callback")
    public ResponseEntity<Map<String,String>> getMethodName(@RequestParam Map<String, String> requestParams) {

        String orderId = requestParams.get("vnp_TxnRef"); 
       Map<String, String> response = new HashMap<>();
    response.put("RspCode", "99");
    response.put("Message", "Unknown error");

    ticketPaymentService.vnPayCallBack(requestParams, response);
    signalClient(response, orderId);
    return ResponseEntity.ok(response);

    }

    public void signalClient(Map<String,String> response,String orderId)
    {
        Map<String, Object> socketPayload = new HashMap<>();
        socketPayload.put("orderId", orderId);

        if ("00".equals(response.get("RspCode"))) {
            
            socketPayload.put("status", "SUCCESS");
            socketPayload.put("message", "Thanh toán thành công!");

            // BẮN TÍN HIỆU THUẦN SOCKET XUỐNG CLIENT ĐANG ĐỢI
            messagingTemplate.convertAndSend("/topic/payment/" + orderId, socketPayload);
            System.out.println("/topic/payment/"+orderId);
        } else {

            socketPayload.put("status", "FAILED");
            socketPayload.put("message", "Giao dịch không thành công hoặc đã bị hủy.");

            // BẮN TÍN HIỆU THẤT BẠI XUỐNG
            messagingTemplate.convertAndSend("/topic/payment/" + orderId, socketPayload);
            System.out.println("/topic/payment/"+orderId);
        }

    }
    
    
    
    
}
