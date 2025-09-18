package com.example.jwtsecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String email;
    
    public JwtResponse(String accessToken, Long id, String username, String email) {
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
