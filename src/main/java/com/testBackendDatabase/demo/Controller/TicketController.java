package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.DTO.BasicTicketDTO;
import com.testBackendDatabase.demo.DTO.TicketDTO;
import com.testBackendDatabase.demo.DTO.TicketForUserDTO;
import com.testBackendDatabase.demo.Request.TicketBookingRequest;
import com.testBackendDatabase.demo.Service.TicketService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping("/bookTickets")
    public ResponseEntity<List<TicketDTO>> bookTickets(@RequestBody TicketBookingRequest request) {
       List<TicketDTO> ticketDTOs=ticketService.BookTicket(request);
       return ResponseEntity.ok(ticketDTOs);

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

    @PostMapping("/validateQR")
    public ResponseEntity<String> validateQR(
            @RequestParam String ticketCode,
            @RequestParam Long showtimeId) {
        String result = ticketService.validateTicket(ticketCode, showtimeId);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/checkInTicket")
    public ResponseEntity<String> checkInTicket(@RequestParam String ticketCode) {
        String result = ticketService.checkInTicket(ticketCode);

        if (result.contains("thành công")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    
    
    
}
