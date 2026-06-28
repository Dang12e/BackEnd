package com.testBackendDatabase.demo.Service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.model.Account;
import com.testBackendDatabase.demo.DTO.WalletTransactionDTO;
import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.WalletTransactionRepository;
import com.testBackendDatabase.demo.model.WalletTransaction;

@Service
public class WalletTransactionService {
    private final WalletTransactionRepository walletTransactionRepository;
    private final AccountRepository accountRepository;

    WalletTransactionService(WalletTransactionRepository walletTransactionRepository,AccountRepository accountRepository)
    {
     this.walletTransactionRepository= walletTransactionRepository;
     this.accountRepository=accountRepository;
    }

    @Transactional(readOnly=true)
    public List<WalletTransactionDTO> getTransactionHistory()
    {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account= accountRepository.findByUsername(username).orElseThrow(
            ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"khong tim thay tai khoan")
        );
        List<WalletTransaction> walletTransactions=walletTransactionRepository.findByAccount_id(account.getId());

        return walletTransactions.stream().map(w->WalletTransactionDTO.builder()
        .amount(w.getAmount())
        .description(w.getDescription())
        .transactionTime(w.getTransactionTime())
        .type(w.getType())
        .build()
        ).toList();
    }
}
