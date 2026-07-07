package com.testBackendDatabase.demo.Chatbot;
import java.util.List;
import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.model.Account;
import com.testBackendDatabase.demo.model.MessageResponse;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") 
public class ChatController {

    private final ChatbotService chatbotService;
    private final AccountRepository accountRepository;
    private final ChatContextService chatContextService;

    public ChatController(ChatbotService chatbotService, AccountRepository accountRepository, ChatContextService chatContextService) {
        this.chatbotService = chatbotService;
        this.accountRepository = accountRepository;
        this.chatContextService = chatContextService;
    }

    
    @PostMapping
    public ResponseEntity<String> chat(@RequestBody Map<String, String> request) {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        Account account= accountRepository.findByUsername(username).orElseThrow(
            ()->new ResponseStatusException(HttpStatus.NOT_FOUND,"không tìm thấy tài khoản"));
        
        List<MessageResponse> context= chatContextService.getChatContext(account.getId());
        String userMessage = request.get("message");
        String response = chatbotService.getChatResponse(userMessage,context,account.getId());
        return ResponseEntity.ok(response);
    }
}