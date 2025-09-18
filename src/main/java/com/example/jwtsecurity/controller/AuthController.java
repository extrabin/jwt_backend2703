package com.example.jwtsecurity.controller;

import com.example.jwtsecurity.dto.*;
import com.example.jwtsecurity.entity.User;
import com.example.jwtsecurity.repository.UserRepository;
import com.example.jwtsecurity.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenFromUsername(loginRequest.getUsername());

            User userPrincipal = (User) authentication.getPrincipal();

            JwtResponse jwtResponse = new JwtResponse(jwt,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getEmail());

            return ResponseEntity.ok(ApiResponse.success("로그인 성공", jwtResponse));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("아이디 또는 비밀번호가 잘못되었습니다."));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("오류: 이미 사용 중인 사용자명입니다!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("오류: 이미 사용 중인 이메일입니다!"));
        }

        // 새 사용자 계정 생성
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("사용자가 성공적으로 등록되었습니다!"));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
                    
                    UserInfoResponse userInfo = new UserInfoResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getCreatedAt()
                    );
                    
                    return ResponseEntity.ok(ApiResponse.success("유효한 토큰입니다.", userInfo));
                } else {
                    return ResponseEntity.status(401)
                            .body(ApiResponse.error("토큰이 만료되었거나 유효하지 않습니다."));
                }
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("토큰이 제공되지 않았습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("토큰이 만료되었거나 유효하지 않습니다."));
        }
    }
}
