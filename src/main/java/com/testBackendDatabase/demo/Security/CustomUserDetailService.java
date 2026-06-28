package com.testBackendDatabase.demo.Security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.model.Account;

@Service
public class CustomUserDetailService implements UserDetailsService{
    private final AccountRepository accountRepository;

    CustomUserDetailService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account=accountRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("không tim thay ten user "));

        return org.springframework.security.core.userdetails.User
                .withUsername(account.getUsername()) // Ném username vào hộ chiếu
                .password(account.getPassword())     // Ném mật khẩu đã băm vào hộ chiếu
                .authorities(account.getRole())      // Ném quyền (Role) vào hộ chiếu
                .build();
    }
    
}
