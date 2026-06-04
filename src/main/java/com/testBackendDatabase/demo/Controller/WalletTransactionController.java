package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.DTO.WalletTransactionDTO;
import com.testBackendDatabase.demo.Service.WalletTransactionService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("api/transaction")
public class WalletTransactionController {
    
    private final WalletTransactionService walletTransactionService;

    WalletTransactionController(WalletTransactionService walletTransactionService)
    {
        this.walletTransactionService = walletTransactionService;
    }

    @GetMapping("getHistory")
    public ResponseEntity<List<WalletTransactionDTO>> getMethodName() {
        List<WalletTransactionDTO> walletTransactionDTOs=walletTransactionService.getTransactionHistory();
        return ResponseEntity.ok(walletTransactionDTOs);
    }
    
    
}
