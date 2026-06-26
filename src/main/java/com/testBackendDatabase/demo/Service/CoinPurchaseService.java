package com.testBackendDatabase.demo.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.WalletTransactionRepository;
import com.testBackendDatabase.demo.VNPAY.PaymentService;
@Service
public class CoinPurchaseService {

    private final AccountRepository accountRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final PaymentService paymentService;

    public CoinPurchaseService(AccountRepository accountRepository,WalletTransactionRepository walletTransactionRepository,PaymentService paymentService)
    {
        this.accountRepository=accountRepository;
        this.walletTransactionRepository=walletTransactionRepository;
        this.paymentService=paymentService;
    }
    @Transactional
    public void GeneratePurchaseRequest(int option)
    {
       
    }
    @Transactional
    public void vnPayCallBack()
    {

    }
    
}
