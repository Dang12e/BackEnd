package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.DTO.AccountDTO;
import com.testBackendDatabase.demo.Service.AccountService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@RestController
@RequestMapping("api/account")
public class AccountController {


    private final AccountService accountService;

    AccountController (AccountService accountService)
    {
       this.accountService= accountService;
    }

    @GetMapping("getInfo")
    public ResponseEntity<AccountDTO> getAccountInfo() {
        AccountDTO accountDTO=accountService.getAccountInfo();
        return ResponseEntity.ok(accountDTO);
    }
    @GetMapping("/all")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {

        return ResponseEntity.ok(accountService.getAllAccounts());

    }
    
}
