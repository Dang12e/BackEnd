package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.DTO.TicketForUserDTO;
import com.testBackendDatabase.demo.Service.TicketService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("api/Ticket")
public class TicketController {

    private final TicketService ticketService;

    TicketController(TicketService ticketService)
    {
        this.ticketService= ticketService;
    }
    
    @GetMapping("/getTickets")
    public ResponseEntity<List<TicketForUserDTO>> getMethodName() {

        List<TicketForUserDTO> ticketForUserDTOs= ticketService.getTickets();
        return ResponseEntity.ok(ticketForUserDTOs);
        
    }
    
    
}
