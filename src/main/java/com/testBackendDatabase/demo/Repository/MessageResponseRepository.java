package com.testBackendDatabase.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.testBackendDatabase.demo.model.MessageResponse;

import java.util.List;


public interface MessageResponseRepository extends JpaRepository<MessageResponse, Long> {

    
    List<MessageResponse> findTop3ByAccountIdOrderByCreatedAtDesc(Long accountId);
}

