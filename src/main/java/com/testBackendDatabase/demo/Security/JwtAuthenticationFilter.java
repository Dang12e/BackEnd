package com.testBackendDatabase.demo.Security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService; // Gọi Bước 2 ra phối hợp

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Thò tay vào Header lấy chuỗi Token ra
            String jwt = getJwtFromRequest(request);

            // 2. Nếu có Token và Token đó hợp lệ (Đúng chữ ký, còn hạn)
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                
                // 3. Lấy username từ Token ra
                String username = jwtTokenProvider.getUsernameFromJWT(jwt);

                // 4. Lấy thông tin User gốc trong DB lên để xác nhận danh tính
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // 5. Tạo một cái "Vé thông hành CHÍNH THỨC" (Đã xác thực thành công)
                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // 6. Đóng dấu thông qua! Nhét cái vé này vào hệ thống của Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Không thể xác thực người dùng bằng JWT", ex);
        }

        // Cho phép request tiếp tục đi tiếp vào Controller
        filterChain.doFilter(request, response);
    }

    // Hàm phụ trợ dùng để bóc tách chữ "Bearer " lấy phần ruột Token
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Cắt bỏ 7 ký tự "Bearer " để lấy lõi Token
        }
        return null;
    }
}
