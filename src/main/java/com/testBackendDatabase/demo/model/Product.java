package com.testBackendDatabase.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity // Đánh dấu đây là một bảng trong DB
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id // Đánh dấu đây là khóa chính (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID (Auto Increment)
    private Long id; 
    
    private String name;
    private double price;


    
}