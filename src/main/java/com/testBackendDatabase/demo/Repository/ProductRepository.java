package com.testBackendDatabase.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testBackendDatabase.demo.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Không cần viết code ở đây, JpaRepository đã làm sẵn các hàm save(), findAll(), findById(),...
}