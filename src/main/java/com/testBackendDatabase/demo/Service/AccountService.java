package com.testBackendDatabase.demo.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.testBackendDatabase.demo.DTO.AccountDTO;
import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.model.Account;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Transactional(readOnly=true)
    public AccountDTO getAccountInfo()
    {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Account account = accountRepository.findByUsername(username).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Khong tim thay ten nguoi dung"));
        return AccountDTO.builder().email(account.getEmail()).id(account.getId())
        .role(account.getRole()).username(account.getUsername()).build();

    }
    
}
