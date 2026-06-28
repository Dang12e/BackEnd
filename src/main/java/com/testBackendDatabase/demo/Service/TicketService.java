package com.testBackendDatabase.demo.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.DTO.BasicTicketDTO;
import com.testBackendDatabase.demo.DTO.TicketDTO;
import com.testBackendDatabase.demo.DTO.TicketForUserDTO;
import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.SeatRepository;
import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import com.testBackendDatabase.demo.Repository.TicketRepository;
import com.testBackendDatabase.demo.model.Account;
import com.testBackendDatabase.demo.model.Seat;
import com.testBackendDatabase.demo.model.ShowTime;
import com.testBackendDatabase.demo.model.Ticket;


@Service
public class TicketService {
    private final int sizePage = 10;
    
    // Tất cả các Repository và Service giờ đều là final
    private final AccountRepository accountRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final ShowTimeRepository showTimeRepository;
    private final QRCodeService qrCodeService;

    
    public TicketService(
            AccountRepository accountRepository,
            TicketRepository ticketRepository,
            SeatRepository seatRepository,
            ShowTimeRepository showTimeRepository,
            QRCodeService qrCodeService) {
        this.accountRepository = accountRepository;
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.showTimeRepository = showTimeRepository;
        this.qrCodeService = qrCodeService;
    }
    

    @Transactional
    public Long BookTicket(@NonNull List<Long> seatIDs, Long showTimeID, Account account)
    {
        try{
       
       if (account == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Tài khoản không hợp lệ");
       }

       ShowTime showTime = showTimeRepository.findActiveByIdWithMovieAndRoom(showTimeID,LocalDateTime.now())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Suất chiếu không tồn tại"));

       List<Seat> seats = seatRepository.findAllById(seatIDs);
       if (seats.size() != seatIDs.size()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Một số ghế không tồn tại");
    }

    List<Long> TicketsInDatabase= ticketRepository.findBookedSeatIds(showTime.getId(), seatIDs);
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


     return (long)totalAmount;
  
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
@Transactional(readOnly=true)
public List<BasicTicketDTO> getUsersTickets()
{
     Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    Account account=accountRepository.findByUsername(username).orElseThrow(
        ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"khong tim thay tai khoan")
    );
    List<Ticket> tickets= ticketRepository.findAllByAccountId(account.getId());
    return tickets.stream().map(t->
       BasicTicketDTO.builder().bookingTime(t.getBookingTime())
       .customerName(username)
       .movieTitle(t.getShowTime().getMovie().getTitle())
       .price(t.getPrice())
       .roomName(t.getShowTime().getShowRoom().getRoomName())
       .seatName(t.getSeat().getName())
       .seatType(t.getSeat().getType())
       .startTime(t.getShowTime().getStartTime())
       .ticketCode(t.getTicketCode())
       .build()
    ).toList();
}
@Transactional(readOnly = true)
public TicketDTO getTicketDetail(String TicketCode)
{
    Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();
    Account account=accountRepository.findByUsername(username).orElseThrow(
        ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"khong tim thay tai khoan")
    );
    Ticket ticket=ticketRepository.findByAccountAndTicketCodeFetchJoin(account.getId(), TicketCode).orElseThrow(
        ()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Ticket code bi loi hoac khong ton tai")
    );
    return TicketDTO.builder().bookingTime(ticket.getBookingTime())
    .customerName(account.getUsername())
    .id(ticket.getId())
    .movieTitle(ticket.getShowTime().getMovie().getTitle())
    .price(ticket.getPrice())
    .qrCodeBase64(ticket.getQrCode())
    .roomName(ticket.getShowTime().getShowRoom().getRoomName())
    .seatName(ticket.getSeat().getName())
    .seatType(ticket.getSeat().getType())
    .startTime(ticket.getShowTime().getStartTime())
    .ticketCode(ticket.getTicketCode())
    .build();

}
@Transactional(readOnly = true)
public Page<BasicTicketDTO> getTicketsWithPageForUser(int page)
{
    Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    String name = authentication.getName();
    Account account= accountRepository.findByUsername(name).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"không tìm thấy tên người dùng"));
    Pageable pageable= PageRequest.of(page,sizePage);
    Page<Ticket> result= ticketRepository.findByAccountId(account.getId(), pageable);
    return result.map(ticket->
        BasicTicketDTO.builder().bookingTime(ticket.getBookingTime())
        .customerName(name)
        .movieTitle(ticket.getShowTime().getMovie().getTitle())
        .price(ticket.getPrice())
        .roomName(ticket.getShowTime().getShowRoom().getRoomName()+", "+ticket.getShowTime().getShowRoom().getCinema().getName() +", "+ticket.getShowTime().getShowRoom().getCinema().getAddress())
        .seatName(ticket.getSeat().getName())
        .seatType(ticket.getSeat().getType())
        .startTime(ticket.getShowTime().getStartTime())
        .ticketCode(ticket.getTicketCode())
        .build()
        
    );
}
}
