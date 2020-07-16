package com.billing.system.springsecurityjwt.controller;

import com.billing.system.springsecurityjwt.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String role;

}
