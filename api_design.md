# JWT Security API 설계서

## 개요

SPA(Single Page Application) 형태의 회원가입/로그인 시스템을 위한 REST API 설계서입니다.

### 기본 정보
- **Base URL**: `http://localhost:8080`
- **인증 방식**: JWT (Json Web Token)
- **토큰 만료시간**: 2시간 (7200초)
- **응답 형식**: JSON
- **문자 인코딩**: UTF-8

## 공통 사항

### 응답 형식

#### 성공 응답
```json
{
  "success": true,
  "message": "성공 메시지",
  "data": { /* 데이터 객체 */ }
}
```

#### 실패 응답
```json
{
  "success": false,
  "message": "에러 메시지"
}
```

### HTTP 상태 코드
- `200 OK`: 요청 성공
- `400 Bad Request`: 잘못된 요청 (유효성 검증 실패 등)
- `401 Unauthorized`: 인증 실패 또는 토큰 만료
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류

### 인증 헤더
```
Authorization: Bearer {JWT_TOKEN}
```

---

## 1. 인증 관련 API

### 1.1 회원가입

#### 요청
```http
POST /api/auth/signup
Content-Type: application/json
```

#### 요청 본문
```json
{
  "username": "string",    // 필수, 3-20자
  "email": "string",       // 필수, 유효한 이메일 형식
  "password": "string"     // 필수, 6-40자
}
```

#### 응답 예시

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "사용자가 성공적으로 등록되었습니다!"
}
```

**실패 (400 Bad Request)**
```json
{
  "success": false,
  "message": "오류: 이미 사용 중인 사용자명입니다!"
}
```

#### 유효성 검증 규칙
- `username`: 3-20자, 필수
- `email`: 유효한 이메일 형식, 필수
- `password`: 6-40자, 필수

---

### 1.2 로그인

#### 요청
```http
POST /api/auth/signin
Content-Type: application/json
```

#### 요청 본문
```json
{
  "username": "string",    // 필수
  "password": "string"     // 필수
}
```

#### 응답 예시

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYzOTQ4OTIwMCwiZXhwIjoxNjM5NDk2NDAwfQ.abc123",
    "tokenType": "Bearer",
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

**실패 (400 Bad Request)**
```json
{
  "success": false,
  "message": "아이디 또는 비밀번호가 잘못되었습니다."
}
```

---

### 1.3 토큰 유효성 검증

#### 요청
```http
POST /api/auth/validate-token
Authorization: Bearer {JWT_TOKEN}
```

#### 응답 예시

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "유효한 토큰입니다.",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

**실패 (401 Unauthorized)**
```json
{
  "success": false,
  "message": "토큰이 만료되었거나 유효하지 않습니다."
}
```

---

## 2. 사용자 관련 API

### 2.1 사용자 프로필 조회

#### 요청
```http
GET /api/user/profile
Authorization: Bearer {JWT_TOKEN}
```

#### 응답 예시

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "프로필 조회 성공",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

**실패 (401 Unauthorized)**
```json
{
  "success": false,
  "message": "인증되지 않은 사용자입니다."
}
```

---

### 2.2 현재 사용자 정보 조회

#### 요청
```http
GET /api/user/me
Authorization: Bearer {JWT_TOKEN}
```

#### 응답 예시

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "사용자 정보 조회 성공",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "createdAt": "2024-01-01T12:00:00"
  }
}
```

---

### 2.3 테스트 엔드포인트

#### 요청
```http
GET /api/user/test
Authorization: Bearer {JWT_TOKEN}
```

#### 응답 예시

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "인증된 사용자만 접근 가능한 테스트 엔드포인트입니다."
}
```

---

## 3. 공개 API

### 3.1 서버 상태 확인

#### 요청
```http
GET /api/
```

#### 응답 예시

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "JWT Security API 서버가 정상적으로 동작 중입니다."
}
```

---

### 3.2 공개 엔드포인트

#### 요청
```http
GET /api/public
```

#### 응답 예시

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "공개 엔드포인트 - 인증 없이 접근 가능합니다."
}
```

---

## 4. 에러 코드 및 메시지

### 4.1 인증 관련 에러

| 에러 메시지 | 설명 | HTTP 상태 |
|------------|------|-----------|
| "아이디 또는 비밀번호가 잘못되었습니다." | 로그인 실패 | 400 |
| "토큰이 만료되었습니다. 다시 로그인해주세요." | JWT 토큰 만료 | 401 |
| "유효하지 않은 토큰입니다." | JWT 토큰 형식 오류 | 401 |
| "토큰 서명이 유효하지 않습니다." | JWT 서명 검증 실패 | 401 |
| "인증되지 않은 요청입니다. 로그인이 필요합니다." | 인증 헤더 없음 | 401 |

### 4.2 유효성 검증 에러

| 필드 | 에러 메시지 |
|------|------------|
| username | "사용자명은 필수입니다" |
| username | "사용자명은 3-20자 사이여야 합니다" |
| password | "비밀번호는 필수입니다" |
| password | "비밀번호는 6-40자 사이여야 합니다" |
| email | "이메일은 필수입니다" |
| email | "유효한 이메일 형식이 아닙니다" |

### 4.3 중복 데이터 에러

| 에러 메시지 | 설명 |
|------------|------|
| "오류: 이미 사용 중인 사용자명입니다!" | 사용자명 중복 |
| "오류: 이미 사용 중인 이메일입니다!" | 이메일 중복 |

---

## 5. 데이터 모델

### 5.1 User 엔티티

```json
{
  "id": "number",           // 사용자 ID (Primary Key)
  "username": "string",     // 사용자명 (Unique)
  "email": "string",        // 이메일 (Unique)
  "password": "string",     // 암호화된 비밀번호
  "createdAt": "datetime"   // 생성일시
}
```

### 5.2 JWT 토큰 구조

#### Header
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### Payload
```json
{
  "sub": "username",        // 사용자명
  "iat": 1639489200,       // 발급시간 (Unix timestamp)
  "exp": 1639496400        // 만료시간 (Unix timestamp)
}
```

---

## 6. 클라이언트 연동 가이드

### 6.1 로그인 플로우

1. **로그인 요청**: `POST /api/auth/signin`
2. **토큰 저장**: 응답받은 JWT를 로컬스토리지에 저장
3. **인증 헤더 설정**: 이후 요청에 `Authorization: Bearer {token}` 헤더 추가

### 6.2 토큰 관리

#### 로컬스토리지 저장
```javascript
// 로그인 성공 후
localStorage.setItem('accessToken', response.data.accessToken);
localStorage.setItem('userInfo', JSON.stringify({
  id: response.data.id,
  username: response.data.username,
  email: response.data.email
}));
```

#### 토큰 사용
```javascript
const token = localStorage.getItem('accessToken');
fetch('/api/user/profile', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

#### 토큰 만료 처리
```javascript
// 401 응답 시 토큰 만료 처리
if (response.status === 401) {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('userInfo');
  // 로그인 페이지로 리다이렉트
  window.location.href = '/login';
}
```

### 6.3 사용 예시

#### 회원가입
```javascript
const signup = async (userData) => {
  const response = await fetch('/api/auth/signup', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(userData)
  });
  return response.json();
};
```

#### 로그인
```javascript
const login = async (credentials) => {
  const response = await fetch('/api/auth/signin', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(credentials)
  });
  const data = await response.json();
  
  if (data.success) {
    localStorage.setItem('accessToken', data.data.accessToken);
  }
  
  return data;
};
```

#### 인증된 API 호출
```javascript
const getProfile = async () => {
  const token = localStorage.getItem('accessToken');
  const response = await fetch('/api/user/profile', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  return response.json();
};
```

---

## 7. 보안 고려사항

### 7.1 JWT 토큰
- **만료시간**: 2시간 (보안과 사용성의 균형)
- **저장위치**: 로컬스토리지 (XSS 취약점 주의)
- **토큰 갱신**: 현재 구현에서는 리프레시 토큰 미사용

### 7.2 비밀번호
- **암호화**: BCrypt 해시 알고리즘 사용
- **최소 길이**: 6자 이상

### 7.3 CORS
- **설정**: 모든 도메인 허용 (개발환경)
- **프로덕션**: 특정 도메인만 허용 권장

### 7.4 HTTPS
- **개발환경**: HTTP 사용
- **프로덕션**: HTTPS 필수 (JWT 토큰 보안)

---

## 8. 환경 설정

### 8.1 데이터베이스
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dev_db?createDatabaseIfNotExist=true&useSSL=true&serverTimezone=Asia/Seoul
spring.datasource.username=${DB_USERNAME:hdcd}
spring.datasource.password=${DB_PASSWORD:1234}
```

### 8.2 JWT 설정
```properties
jwt.secret=mySecretKey1234567890abcdefghijklmnopqrstuvwxyz1234567890
jwt.expiration=7200000  # 2시간 (밀리초)
```

---

## 9. 버전 정보

- **API 버전**: v1.0
- **Spring Boot**: 3.4.9
- **Java**: 17
- **작성일**: 2024-01-01
- **최종 수정일**: 2024-01-01
