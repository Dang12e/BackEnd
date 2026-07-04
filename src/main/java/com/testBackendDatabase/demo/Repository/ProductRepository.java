package com.testBackendDatabase.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.testBackendDatabase.demo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Không cần viết code ở đây, JpaRepository đã làm sẵn các hàm save(), findAll(), findById(),...
}