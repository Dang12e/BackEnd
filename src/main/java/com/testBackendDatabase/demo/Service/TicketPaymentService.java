package com.testBackendDatabase.demo.Service;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.testBackendDatabase.demo.DTO.BookingPaymentResponse;
import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.OrderRepository;
import com.testBackendDatabase.demo.Repository.SeatRepository;
import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import com.testBackendDatabase.demo.Repository.TicketHoldingRepository;
import com.testBackendDatabase.demo.Repository.TicketRepository;
import com.testBackendDatabase.demo.Repository.WalletTransactionRepository;
import com.testBackendDatabase.demo.Request.TicketBookingRequest;
import com.testBackendDatabase.demo.VNPAY.PaymentService;
import com.testBackendDatabase.demo.model.Order;
import com.testBackendDatabase.demo.model.Seat;
import com.testBackendDatabase.demo.model.ShowTime;
import com.testBackendDatabase.demo.model.TicketHolding;
import com.testBackendDatabase.demo.model.WalletTransaction;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;

@Service
@Validated
public class TicketPaymentService {
    @Value("${vnp.hashSecret}") // Cấu hình secret key trong application.properties của bạn
    private String hashSecret;

    private final TicketService ticketService;
    private final AccountRepository accountRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final PaymentService paymentService;
    private final SeatRepository seatRepository;
    private final OrderRepository orderRepository;
    private final OrderTimeoutManager timeoutManager;
    private final SimpMessagingTemplate messagingTemplate;
    private final TicketRepository ticketRepository;
    private final ShowTimeRepository showTimeRepository;
    private final TicketHoldingRepository ticketHoldingRepository;

    public TicketPaymentService(AccountRepository accountRepository,
                                WalletTransactionRepository walletTransactionRepository,
                                OrderRepository orderRepository,
                                PaymentService paymentService,
                                SeatRepository seatRepository,
                                TicketService ticketService,
                                OrderTimeoutManager timeoutManager,
                                SimpMessagingTemplate messagingTemplate
                            ,TicketRepository ticketRepository
                        ,ShowTimeRepository showTimeRepository
                    ,TicketHoldingRepository ticketHoldingRepository) {
        this.ticketService = ticketService;
        this.accountRepository = accountRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.seatRepository = seatRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.timeoutManager = timeoutManager;
        this.messagingTemplate = messagingTemplate;
        this.ticketRepository= ticketRepository;
        this.showTimeRepository=showTimeRepository;
        this.ticketHoldingRepository=ticketHoldingRepository;
    }

    /**
     * Hàm sinh URL thanh toán dựa trên Option cố định và User cố định
     * @param option Gói nạp do người dùng chọn (1, 2, 3...)
     * @param userId ID của User thực hiện nạp (lấy từ Token/Session)
     */
  @Transactional
  @SuppressWarnings("null")
public BookingPaymentResponse generateBookingPaymentURL(@Valid @NonNull TicketBookingRequest bookingRequest, HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = Objects.requireNonNull(authentication.getName());

    if (bookingRequest.getSeatIds() == null || bookingRequest.getSeatIds().isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one seat must be selected");
    }

    var account = accountRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));
    ShowTime showTime= showTimeRepository.findActiveShowTimeById((bookingRequest.getShowtimeId()),LocalDateTime.now()).orElseThrow(()->new 
     ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy suất chiểus hoăc suất chiếu hết hạn"));
    List<Long> bookedTicketInDataBase= ticketRepository.findBookedSeatIds(bookingRequest.getShowtimeId(),bookingRequest.getSeatIds());
    
    if(!bookedTicketInDataBase.isEmpty())
    {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"co mot so ghe da dat");
    }

    List<Seat> seats = seatRepository.findAllById(bookingRequest.getSeatIds());
    
    if (seats.size() != bookingRequest.getSeatIds().size()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some seats do not exist");
    }
    long totalAmount = calculateTotalPrice(seats);

    String seatIdsStr = bookingRequest.getSeatIds().stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
    
    
    Order order = Objects.requireNonNull(Order.builder()
            .account(account)
            .showTime(showTime)
            .totalAmount((double) totalAmount)
            .status("PENDING")
            .createdAt(LocalDateTime.now())
            .seatIdsJson(seatIdsStr)
            .build());
    
    Order savedOrder = Objects.requireNonNull(orderRepository.save(order));

    // Create and save ticket holdings with exception handling
    try {
        createAndSaveTicketHoldings(bookingRequest, savedOrder.getId(), username, 10);
    } catch (DataIntegrityViolationException e) {
        // Seats are already being held by another user
        orderRepository.delete(savedOrder);
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Some seats are already being held by another user");
    } catch (Exception e) {
        // Unexpected error - cleanup order and rethrow
        orderRepository.delete(savedOrder);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reserving seats: " + e.getMessage());
    }
    
    timeoutManager.scheduleOrderTimeout(savedOrder.getId(), username);
    String txnRef = "ORD_" + order.getId(); 
    String orderInfo = "Thanh toan don hang ve #" + order.getId() + " cho tai khoan: " + username;
    
    return BookingPaymentResponse.builder().paymentUrl(paymentService.createPaymentUrl(totalAmount, orderInfo, txnRef, request))
    .orderId(savedOrder.getId()).build();
}
    
@Transactional
@SuppressWarnings("null")
public void vnPayCallBack(Map<String, String> queryParams,Map<String, String> response) {
    
    response.put("RspCode", "00");
    response.put("Message", "Don hang xu li thanh cong");

    if (!isValidVnPaySignature(queryParams)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid Checksum");
            return;
        }

    String responseCode = queryParams.get("vnp_ResponseCode");
    String txnRef = queryParams.get("vnp_TxnRef");

    checkTxnRef(txnRef);

    Long orderId = Long.parseLong(txnRef.replace("ORD_", ""));
        
    Order order = orderRepository.findByIdForUpdate(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn!"));
    String username = order.getAccount().getUsername();
    Long showTimeID= order.getShowTime().getId();

    if ("00".equals(responseCode) && txnRef != null) {
        if (walletTransactionRepository.existsByTxnRefAndStatus(txnRef, "SUCCESS")) {
            messagingTemplate.convertAndSendToUser(username, "/queue/payment-status", 
                Map.of("status", "SUCCESS", "orderId", orderId, "message", "Order already processed"));
                response.put("RspCode", "01");
                response.put("Message", "don hang tu vnpay bi loi hoac thieu txnRef");
                return;
            
        }
        
        if ("SUCCESS".equals(order.getStatus()))
            {
                response.put("RspCode", "02");
                response.put("Message", "Don hang da duoc xu li thanh cong");
                 return ;
            }

        List<Long> seatIds = Arrays.stream(order.getSeatIdsJson().split(","))
                .map(Long::parseLong)
                .toList();

        try {
            ticketService.BookTicket(seatIds, showTimeID, order.getAccount());
            
            // Mark order as SUCCESS in its own transaction (prevents rollback)
            markOrderAsSuccess(orderId);
            
            createWalletTransaction(order, orderId, txnRef, "SUCCESS");
            
            ticketHoldingRepository.deleteByOrderId(orderId);
            
            messagingTemplate.convertAndSendToUser(
                            username, 
                            "/queue/payment-status", 
                            Map.of("status", "SUCCESS", "orderId", orderId, "message", "Thanh toán thành công!")
                    );
            response.put("RspCode", "00");
            response.put("Message", "don hang xu lí thanh cong ");
        } catch (Exception e) {
            // Mark order as FAILED in its own transaction (commits independently)
            try {
                markOrderAsFailed(orderId, showTimeID);
            } catch (Exception ex) {
                // Log if marking as failed fails, but don't break the process
                System.err.println("Failed to mark order as FAILED: " + ex.getMessage());
                response.put("RspCode", "99");
                response.put("Message", "co loi phi server");
                return;
            }
            
            // Clean up holdings and notify user
            try {
                ticketHoldingRepository.deleteByOrderId(orderId);
            } catch (Exception ex) {
                System.err.println("Failed to delete ticket holdings: " + ex.getMessage());
                response.put("RspCode", "99");
                response.put("Message", "co loi phi server");
                return;
            }
            
            messagingTemplate.convertAndSendToUser(
                            username, 
                            "/queue/payment-status", 
                            Map.of("status", "FAILED", "orderId", orderId, "message", "Lỗi khi tạo vé: " + e.getMessage())
                    );
            response.put("RspCode", "99");
            response.put("Message", "co loi phi server");
            return;
        }
        
    }
    else
    {
        // Payment failed - mark order as FAILED in its own transaction
        try {
            markOrderAsFailed(orderId, showTimeID);
        } catch (Exception ex) {
            System.err.println("Failed to mark order as FAILED: " + ex.getMessage());
            response.put("RspCode", "99");
            response.put("Message", "co loi phi server");
            return;
        }
        
        // Clean up holdings
        try {
            ticketHoldingRepository.deleteByOrderId(orderId);
        } catch (Exception ex) {
            System.err.println("Failed to delete ticket holdings: " + ex.getMessage());
            response.put("RspCode", "99");
            response.put("Message", "co loi phi server");
            return;
        }

            response.put("RspCode", "99");
            response.put("Message", "co loi phi server");
            
        
        messagingTemplate.convertAndSendToUser(
                username, 
                "/queue/payment-status", 
                Map.of("status", "FAILED", "orderId", orderId, "message", "Giao dịch bị hủy hoặc thất bại!")
        );
        return;
    }
    response.put("RspCode", "00");
    response.put("Message", "don hang xu li thanh cong");
    return;
    
}
public Long calculateTotalPrice(List<Seat> seats) {
        long totalAmount = 0;
        for (Seat seat : seats) {
            double price = seat.getBasePrice();
            if ("VIP".equals(seat.getType())) {
                price = price * 1.5; 
            }
            totalAmount += (long) price; // Ép kiểu cẩn thận cuối luồng
        }
        return totalAmount;
    }

    /**
     * Create and save TicketHolding list to database
     * @param bookingRequest The booking request containing showtime and seat IDs
     * @param orderId The order ID associated with this booking
     * @param username The username of the account making the booking
     * @param holdDurationMinutes The duration in minutes to hold the seats (e.g., 10 minutes)
     */
    @Transactional
    public void createAndSaveTicketHoldings(TicketBookingRequest bookingRequest, Long orderId, String username, int holdDurationMinutes) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(holdDurationMinutes);
        
        List<TicketHolding> ticketHoldings = Objects.requireNonNull(bookingRequest.getSeatIds().stream()
                .map(seatId -> TicketHolding.builder()
                        .showtimeId(bookingRequest.getShowtimeId())
                        .seatId(seatId)
                        .orderId(orderId)
                        .username(username)
                        .expiresAt(expiresAt)
                        .build())
                .toList());
        
        ticketHoldingRepository.saveAll(ticketHoldings);
    }
    public void createWalletTransaction(Order order,Long orderId, String txnRef,String Status)
    {
      WalletTransaction walletTransaction = Objects.requireNonNull(WalletTransaction.builder()
                    .account(order.getAccount())
                    .amount(order.getTotalAmount())
                    .description("Thanh toan hoa don ve #" + orderId)
                    .transactionTime(LocalDateTime.now())
                    .type("TICKET_BUY_DIRECT")
                    .status("SUCCESS")
                    .txnRef(txnRef)
                    .build());
            walletTransactionRepository.save(walletTransaction);
    }

    /**
     * Mark order as FAILED in a separate transaction
     * This ensures the FAILED status is saved even if outer transaction rolls back
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void markOrderAsFailed(@NonNull Long orderId, Long showtimeId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("FAILED");
        orderRepository.save(order);
    }

    /**
     * Mark order as SUCCESS in a separate transaction
     * This ensures the SUCCESS status is saved before any subsequent operations
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void markOrderAsSuccess(@NonNull Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus("SUCCESS");
        orderRepository.save(order);
    }
    public void checkTxnRef(String txnRef)
    {
      if (txnRef == null) {
        throw new IllegalArgumentException("Invalid callback: txnRef is required");
    }
    }

    private boolean isValidVnPaySignature(Map<String, String> requestParams) {
        String vnp_SecureHash = requestParams.get("vnp_SecureHash");
        if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
            return false;
        }

        // Sắp xếp các tham số theo thứ tự bảng chữ cái bảng mã ASCII bằng TreeMap
        Map<String, String> sortedFields = new TreeMap<>(requestParams);
        sortedFields.remove("vnp_SecureHash");
        sortedFields.remove("vnp_SecureHashType");

        // Tiến hành nối chuỗi query
        StringBuilder signData = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedFields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value != null && !value.isEmpty()) {
                signData.append(key).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII)).append('&');
            }
        }
        
        // Cắt bỏ ký tự '&' cuối cùng
        if (signData.length() > 0) {
            signData.setLength(signData.length() - 1);
        }

        // Thực hiện băm dữ liệu bằng HMAC-SHA512 để đối chiếu
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(hashSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(signData.toString().getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            
            return sb.toString().equalsIgnoreCase(vnp_SecureHash);
        } catch (Exception e) {
            return false;
        }
    }
}

