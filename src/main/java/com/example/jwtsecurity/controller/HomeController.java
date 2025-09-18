package com.example.jwtsecurity.controller;

import com.example.jwtsecurity.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok(ApiResponse.success("JWT Security API 서버가 정상적으로 동작 중입니다."));
    }

    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("공개 엔드포인트 - 인증 없이 접근 가능합니다."));
    }
}
