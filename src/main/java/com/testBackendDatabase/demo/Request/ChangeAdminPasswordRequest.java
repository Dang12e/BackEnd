package com.testBackendDatabase.demo.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeAdminPasswordRequest {

    private Long adminId;

    private String oldPassword;

    private String newPassword;

}