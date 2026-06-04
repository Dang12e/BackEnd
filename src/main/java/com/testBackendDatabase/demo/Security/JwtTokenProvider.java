package com.testBackendDatabase.demo.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 1. Tạo một Chuỗi Bí Mật chỉ có Server biết để ký tên chống giả mạo
    private final String JWT_SECRET = "Hoang_Anh_bi_Gay_Duy_ANh_bi_Gay_Dang_Dep_Trai_123456709";
    
    // Chuyển chuỗi mã thành định dạng Key an toàn của thư viện
    private final Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());

    // 2. Thiết lập thời gian sống của thẻ (Ví dụ: 1 ngày = 86400000 mili-giây)
    private final long JWT_EXPIRATION = 86400000L;

    // LOGIC TẠO CHUỖI TOKEN
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

        // Chuỗi Logic xây dựng JWT bằng Builder (giống cách bạn làm với User)
        return Jwts.builder()
                .setSubject(username) // Ném username vào phần Payload
                .setIssuedAt(now)     // Ghi ngày phát hành thẻ
                .setExpiration(expiryDate) // Ghi ngày hết hạn thẻ
                .signWith(key, SignatureAlgorithm.HS256) // Ký tên bằng Secret Key + Thuật toán băm
                .compact(); // Xuất xưởng chuỗi "aaaaa.bbbbb.ccccc"
    }


    public boolean validateToken(String token) {
    try {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return true;
    } catch (Exception e) {
        return false; // Token sai chữ ký, hết hạn, hoặc bị rách...
    }
}

// ĐOẠN 2: Hàm bóc tách phần Payload để lấy ra chữ "username"
public String getUsernameFromJWT(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
}
}