package com.testBackendDatabase.demo.Security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class RoleChecker {

    public static void CheckRole(String role)
    {
        //KIỂM TRA QUYÊN HẠN
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(role.equals(authentication.getAuthorities()))
        {
            return;
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Không thuộc thẩm quyền");
        }
    }
    
}
