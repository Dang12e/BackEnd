package com.testBackendDatabase.demo.Service;

import com.testBackendDatabase.demo.Request.ChangeAdminPasswordRequest;
import org.springframework.stereotype.Service;

import com.testBackendDatabase.demo.DTO.AdminDTO;
import com.testBackendDatabase.demo.Repository.AdminRepository;
import com.testBackendDatabase.demo.Request.AdminLoginRequest;
import com.testBackendDatabase.demo.model.Admin;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminDTO login(AdminLoginRequest request) {
        System.out.println("Username nhận: " + request.getUsername());
        System.out.println("Password nhận: " + request.getPassword());
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Tài khoản không tồn tại"));
        System.out.println("Username DB: " + admin.getUsername());
        System.out.println("Password DB: " + admin.getPassword());
        if (!admin.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }

        return AdminDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .role(admin.getRole())
                .build();
    }
    public AdminDTO getAdminInfo(Long id) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Admin"));

        return AdminDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .fullName(admin.getFullName())
                .role(admin.getRole())
                .build();
    }
    public void changePassword(ChangeAdminPasswordRequest request) {

        // Tìm Admin theo ID
        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy tài khoản"));

        // Kiểm tra mật khẩu hiện tại
        if (!admin.getPassword().equals(request.getOldPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        // Cập nhật mật khẩu mới
        admin.setPassword(request.getNewPassword());

        // Lưu xuống database
        adminRepository.save(admin);
    }
}