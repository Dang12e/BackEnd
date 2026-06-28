package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.DTO.OrderDTO;
import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.OrderRepository;
import com.testBackendDatabase.demo.Repository.SeatRepository;
import com.testBackendDatabase.demo.Repository.ShowTimeRepository;
import com.testBackendDatabase.demo.model.Account;
import com.testBackendDatabase.demo.model.Order;
import com.testBackendDatabase.demo.model.Seat;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("api/account")
public class OrderHistoryController {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final SeatRepository seatRepository;
    private final int PAGE_SIZE= 10;

    public OrderHistoryController(AccountRepository accountRepository,OrderRepository orderRepository,ShowTimeRepository showTimeRepository, SeatRepository seatRepository){
        this.accountRepository=accountRepository;
        this.orderRepository=orderRepository;
        this.seatRepository = seatRepository;

    }

    @GetMapping("/getOrderHistory")
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrderHistory(@RequestParam int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account=accountRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "tài khoản không tồn tại"));
        Pageable pageable= PageRequest.of(page, PAGE_SIZE);
        Page<Order> orders=orderRepository.findByAccount_id(account.getId(),pageable);

        
        return orders.map(order->{
            List<Long> seatIds = Objects.requireNonNull(Arrays.stream(order.getSeatIdsJson().split(","))
                .map(Long::parseLong)
                .toList());
            String seats="";
            for(Seat seat :seatRepository.findAllById(seatIds))
            {
             seats+=seat.getName()+", ";
            }
            
            return
            OrderDTO.builder().seatIdsJson(seats)
            .status(order.getStatus())
            .totalAmount(order.getTotalAmount())
            .createdAt(order.getCreatedAt())
            .movieName(order.getShowTime().getMovie().getTitle()).build();
        }
        );



       
    }
    
    
}
