package com.testBackendDatabase.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Request.RegisterRequest;
import com.testBackendDatabase.demo.model.Account;


@RestController
@RequestMapping("/api/auth")
public class SignUpController {

    @Autowired 
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> Register(@RequestBody RegisterRequest loginRequest)
    {
        if (accountRepository.findByUsername(loginRequest.getUsername()).isPresent()) {
        return ResponseEntity.badRequest().body("Tên tài khoản này đã tồn tại!");
        }

        if (loginRequest.getPassword().length() < 6) {
        return ResponseEntity.badRequest().body("Mật khẩu phải có ít nhất 6 ký tự!");
        }

        String encodedPassword= passwordEncoder.encode(loginRequest.getPassword());

        Account account= 
        Account.builder().username(loginRequest.getUsername()).
        password(encodedPassword).balance(0d)
        .email(loginRequest.getEmail())
        .role("ROLE_USER").build();

        accountRepository.save(account);

        return ResponseEntity.ok("Đăng ký thành công !");
        

    }
    
    }
    


