package com.testBackendDatabase.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.testBackendDatabase.demo.model.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {

    List<WalletTransaction> findByAccount_id(Long id);

    boolean existsByTxnRefAndStatus(String txnRef, String string);

}
    

