package com.testBackendDatabase.demo.Controller;

import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import com.testBackendDatabase.demo.Repository.TicketRepository;
import com.testBackendDatabase.demo.Repository.WalletTransactionRepository;
import com.testBackendDatabase.demo.DTO.OverViewResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/Overview")
public class OverViewController {

    private final TicketRepository ticketRepository;
    private final ShowTimeRepository showTimeRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public OverViewController(TicketRepository ticketRepository,
                              ShowTimeRepository showTimeRepository,
                              WalletTransactionRepository walletTransactionRepository) {
        this.ticketRepository = ticketRepository;
        this.showTimeRepository = showTimeRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @GetMapping
    public OverViewResponse getOverview() {

        Long totalTickets = ticketRepository.count();

        Long activeShowTimes =
                showTimeRepository.countActiveShowTimes(LocalDateTime.now());

        Double totalRevenue =
                walletTransactionRepository.tinhTongDoanhThu();

        return new OverViewResponse(
                totalRevenue,
                totalTickets,
                activeShowTimes
        );
    }
}