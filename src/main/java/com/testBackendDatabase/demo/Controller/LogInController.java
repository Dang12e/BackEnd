package com.testBackendDatabase.demo.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testBackendDatabase.demo.Request.RegisterRequest;
import com.testBackendDatabase.demo.Security.JwtTokenProvider;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/auth")
public class LogInController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody RegisterRequest request) {
        
        try 
        {
            var TempLoginTicket =new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword());
                    authenticationManager.authenticate(TempLoginTicket);
            String jwtToken=jwtTokenProvider.generateToken(request.getUsername());
            return ResponseEntity.ok(Map.of("token", jwtToken));
                
        }
        catch(Exception e)
        {
            return ResponseEntity.status(401).body("Đăng nhập thất bại: Sai tài khoản hoặc mật khẩu!");
        }
        
        
    }
    
    
    
}
