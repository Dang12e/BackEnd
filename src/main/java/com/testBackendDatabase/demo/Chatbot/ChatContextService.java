package com.testBackendDatabase.demo.Chatbot;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.testBackendDatabase.demo.Repository.AccountRepository;
import com.testBackendDatabase.demo.Repository.MessageResponseRepository;
import com.testBackendDatabase.demo.model.Account;
import com.testBackendDatabase.demo.model.MessageResponse;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatContextService {

    private final MessageResponseRepository repository;
    private final AccountRepository accountRepository;

    public List<MessageResponse> getChatContext(Long accountId) {
        // 1. Lấy 3 tin mới nhất từ DB (Ví dụ: [Tin_3, Tin_2, Tin_1])
        List<MessageResponse> latestMessages = repository.findTop3ByAccountIdOrderByCreatedAtDesc(accountId);
        
        // 2. Tạo bản sao và đảo ngược lại đúng thứ tự thời gian (Ví dụ: [Tin_1, Tin_2, Tin_3])
        List<MessageResponse> context = new ArrayList<>(latestMessages);
        Collections.reverse(context);
        
        return context;
    }

    @Transactional
    public MessageResponse saveChatHistory(@NonNull Long accountId, String userMessage, String aiResponse) {
        // 1. Tìm tài khoản trong DB, nếu không thấy sẽ bắn lỗi
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản với ID: " + accountId));

        // 2. Sử dụng Builder (từ Lombok) để tạo object nhanh gọn
        MessageResponse chatLog = Objects.requireNonNull(MessageResponse.builder()
                .account(account)
                .userMessage(userMessage)
                .aiResponse(aiResponse)
                .build()); // createdAt sẽ tự sinh nhờ @PrePersist

        // 3. Lưu xuống database và trả về entity đã lưu
        return repository.save(chatLog);
    }
}

