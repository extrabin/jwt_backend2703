package com.example.jwtsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
