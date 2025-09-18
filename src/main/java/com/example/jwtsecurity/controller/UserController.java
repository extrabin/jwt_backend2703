package com.example.jwtsecurity.controller;

import com.example.jwtsecurity.dto.ApiResponse;
import com.example.jwtsecurity.dto.UserInfoResponse;
import com.example.jwtsecurity.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        UserInfoResponse userInfo = new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
        
        return ResponseEntity.ok(ApiResponse.success("사용자 정보 조회 성공", userInfo));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("인증되지 않은 사용자입니다."));
        }
        
        User user = (User) authentication.getPrincipal();
        
        UserInfoResponse userInfo = new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
        
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", userInfo));
    }

    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("인증된 사용자만 접근 가능한 테스트 엔드포인트입니다."));
    }
}
