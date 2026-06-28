package com.testBackendDatabase.demo.Controller;

import com.testBackendDatabase.demo.DTO.AdminDTO;
import com.testBackendDatabase.demo.Request.AdminLoginRequest;
import com.testBackendDatabase.demo.Request.ChangeAdminPasswordRequest;
import com.testBackendDatabase.demo.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminDTO> login(@RequestBody AdminLoginRequest request) {
        System.out.println("===== CONTROLLER =====");
        System.out.println(request.getUsername());
        System.out.println(request.getPassword());
        AdminDTO admin = adminService.login(request);

        return ResponseEntity.ok(admin);
    }
    @GetMapping("/info/{id}")
    public ResponseEntity<AdminDTO> getAdminInfo(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAdminInfo(id));
    }
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangeAdminPasswordRequest request) {

        System.out.println("===== CHANGE PASSWORD =====");
        System.out.println("ID = " + request.getAdminId());
        System.out.println("Old = " + request.getOldPassword());
        System.out.println("New = " + request.getNewPassword());

        adminService.changePassword(request);

        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}