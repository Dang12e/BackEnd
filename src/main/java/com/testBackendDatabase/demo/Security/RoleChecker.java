package com.testBackendDatabase.demo.Security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

public class RoleChecker {

    public static void CheckRole(String role)
    {
        //KIỂM TRA QUYÊN HẠN
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(role::equals)) {
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không thuộc thẩm quyền");
    }
    
}
