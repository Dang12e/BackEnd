package com.testBackendDatabase.demo.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.testBackendDatabase.demo.Repository.ProductRepository;
import com.testBackendDatabase.demo.model.Product;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository; // Spring tự động tiêm Bean của Repository vào đây

    // 1. Xử lý POST request để THÊM sản phẩm vào MySQL
    @PostMapping("/add")
    public Product addProduct(@RequestBody Product newProduct) {
        // Hàm save() có sẵn trong JpaRepository sẽ tự động chèn một dòng mới vào bảng Product
        return productRepository.save(newProduct);
    }

    // 2. Xử lý GET request để LẤY TẤT CẢ sản phẩm từ MySQL
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        // Hàm findAll() sẽ lấy toàn bộ các dòng trong bảng Product và chuyển thành List
        return productRepository.findAll();
    }
    
}