package com.testBackendDatabase.demo.Service;

import com.testBackendDatabase.demo.Repository.WalletTransactionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.DTO.TicketDTO;
import com.testBackendDatabase.demo.DTO.TicketForUserDTO;
import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.SeatRepository;
import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import com.testBackendDatabase.demo.Repository.TicketRepository;
import com.testBackendDatabase.demo.Request.TicketBookingRequest;
import com.testBackendDatabase.demo.model.Account;
import com.testBackendDatabase.demo.model.Seat;
import com.testBackendDatabase.demo.model.ShowTime;
import com.testBackendDatabase.demo.model.Ticket;
import com.testBackendDatabase.demo.model.WalletTransaction;


@Service
public class TicketService {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TicketRepository ticketRepository;

    
    @Autowired
    private SeatRepository seatRepository;

    @Autowired ShowTimeRepository showTimeRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired 
    private QRCodeService qrCodeService;

    

    @Transactional
    public List<TicketDTO> BookTicket(TicketBookingRequest request)
    {
        try{
       
       Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
       

       String username = authentication.getName();

       Account account = accountRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

       ShowTime showTime = showTimeRepository.findActiveByIdWithMovieAndRoom(request.getShowtimeId(),LocalDateTime.now())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Suất chiếu không tồn tại"));

       List<Seat> seats = seatRepository.findAllById(request.getSeatIds());
       if (seats.size() != request.getSeatIds().size()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Một số ghế không tồn tại");
    }

    List<Long> TicketsInDatabase= ticketRepository.findBookedSeatIds(showTime.getId(), request.getSeatIds());
    if(!TicketsInDatabase.isEmpty())
    {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"CO mot so ghe da dat");
    }

    double totalAmount = 0;
    for (Seat seat : seats) {
        double price = seat.getBasePrice();
        if ("VIP".equals(seat.getType())) {
            // Logic giá VIP của bạn (có thể nhân hệ số 1.5 như ở code trước)
            price = price * 1.5; 
        }
        totalAmount += price;
    }
    totalAmount=0;//TEst

    if (account.getBalance() < totalAmount) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số dư không đủ để thực hiện giao dịch");
    }

    // 6. Trừ tiền tài khoản
    account.setBalance(account.getBalance() - totalAmount);
    accountRepository.save(account);

    List<Ticket> tickets = new ArrayList<>();
    for (Seat seat : seats) {
        String ticketCode = UUID.randomUUID().toString();

        Ticket ticket = Ticket.builder()
        .ticketCode(ticketCode)
        .qrCode(qrCodeService.generateQRCodeBase64(ticketCode))
                .showTime(showTime)
                .seat(seat)
                .account(account)
                .price(seat.getBasePrice()) // hoặc giá sau khi tính VIP
                .bookingTime(LocalDateTime.now())
                .build();
        tickets.add(ticket);
    }
    ticketRepository.saveAll(tickets);

    // 8. Lưu lịch sử giao dịch (One-to-Many với Account)
    WalletTransaction history = WalletTransaction.builder()
            .amount(totalAmount)
            .type("BOOKING_TICKET")
            .description("Đặt vé suất chiếu: " + showTime.getId())
            .transactionTime(LocalDateTime.now())
            .account(account) // Thiết lập quan hệ
            .build();
    walletTransactionRepository.save(history);

    // 9. Trả về DTO (Bạn có thể map tickets sang TicketDTO)
   return tickets.stream().map(ticket -> 
    TicketDTO.builder()
            .bookingTime(ticket.getBookingTime())
            .customerName(username)
            .id(ticket.getId())
            .movieTitle(ticket.getShowTime().getMovie().getTitle())
            .price(ticket.getPrice())
            .ticketCode(ticket.getTicketCode())
            .qrCodeBase64(ticket.getQrCode())
            .roomName(ticket.getShowTime().getShowRoom().getRoomName())
            .seatName(ticket.getSeat().getName())
            .seatType(ticket.getSeat().getType())
            .startTime(ticket.getShowTime().getStartTime())
            .build()
).collect(Collectors.toList());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }



    
}
@Transactional(readOnly = true)
public List<TicketForUserDTO> getTickets()
{
    Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    Account account=accountRepository.findByUsername(username).orElseThrow(
        ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"khong tim thay tai khoan")
    );
    List<Ticket> tickets= ticketRepository.findAllByAccountId(account.getId());
    return tickets.stream().map(t->
        TicketForUserDTO.builder().bookingTime(t.getBookingTime())
        .customerName(t.getAccount().getUsername())
        .movieTitle(t.getShowTime().getMovie().getTitle())
        .price(t.getPrice())
        .roomName(t.getShowTime().getShowRoom().getRoomName())
        .seatName(t.getSeat().getName())
        .startTime(t.getShowTime().getStartTime())
        .seatType(t.getSeat().getType())
        .ticketCode(t.getTicketCode()).build()
    ).collect(Collectors.toList());
}
}
