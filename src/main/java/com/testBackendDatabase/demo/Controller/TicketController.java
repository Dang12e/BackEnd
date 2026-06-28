package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.DTO.BasicTicketDTO;
import com.testBackendDatabase.demo.DTO.TicketDTO;
import com.testBackendDatabase.demo.DTO.TicketForUserDTO;
import com.testBackendDatabase.demo.Service.TicketService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/Ticket")
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
    @GetMapping("/getUsersTickets")
    public ResponseEntity<List<BasicTicketDTO>> getUsersTickets() {
        List<BasicTicketDTO> basicTicketDTOs=ticketService.getUsersTickets();
        return ResponseEntity.ok(basicTicketDTOs);
        
    }
    @GetMapping("/getTicketDetail")
    public ResponseEntity<TicketDTO> getTicketDetail(@RequestParam String ticketCode) {
        TicketDTO ticketDTO=ticketService.getTicketDetail(ticketCode);
        return ResponseEntity.ok(ticketDTO);
    }
    
    @GetMapping("/getUsersTicketPage")
    public Page<BasicTicketDTO> getUsersTicketPage(@RequestParam int page) {
        return ticketService.getTicketsWithPageForUser(page);
    }
    
    
    
    
    
}
