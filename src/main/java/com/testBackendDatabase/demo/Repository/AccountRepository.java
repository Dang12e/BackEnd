package com.testBackendDatabase.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testBackendDatabase.demo.model.Account;




public interface AccountRepository extends JpaRepository<Account,Long> {

    Optional<Account> findByUsername(String username);
    
}
    
    

