# JWT Security API

SPA 형태의 회원가입/로그인 시스템을 위한 REST API 백엔드입니다.

## 기술 스택

- Java 17
- Spring Boot 3.4.9
- Spring Security
- Spring Data JPA
- MySQL
- JWT (Json Web Token)
- Lombok

## 주요 기능

- 회원가입/로그인
- JWT 기반 인증 (단일 액세스 토큰, 만료시간 2시간)
- 사용자 정보 조회
- 토큰 유효성 검증
- 예외 처리 및 응답 표준화

## 설정

### 1. 데이터베이스 설정

MySQL에서 데이터베이스를 생성합니다:

```sql
CREATE DATABASE jwt_security_db;
```

### 2. application.properties 설정

필요에 따라 데이터베이스 연결 정보를 수정하세요:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/jwt_security_db
spring.datasource.username=root
spring.datasource.password=password
```

### 3. 애플리케이션 실행

```bash
mvn spring-boot:run
```

## API 엔드포인트

### 인증 관련 API

#### 회원가입
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

#### 로그인
```http
POST /api/auth/signin
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**응답:**
```json
{
  "success": true,
  "message": "로그인 성공",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "id": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

#### 토큰 유효성 검증
```http
POST /api/auth/validate-token
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 사용자 관련 API

#### 사용자 정보 조회
```http
GET /api/user/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

#### 현재 사용자 정보
```http
GET /api/user/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 공개 API

#### 서버 상태 확인
```http
GET /api/
```

#### 공개 엔드포인트
```http
GET /api/public
```

## 클라이언트 연동 가이드

### 1. 로그인 처리

```javascript
const login = async (username, password) => {
  try {
    const response = await fetch('/api/auth/signin', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password })
    });
    
    const data = await response.json();
    
    if (data.success) {
      // JWT 토큰을 로컬스토리지에 저장
      localStorage.setItem('accessToken', data.data.accessToken);
      localStorage.setItem('userInfo', JSON.stringify({
        id: data.data.id,
        username: data.data.username,
        email: data.data.email
      }));
      
      return data.data;
    } else {
      throw new Error(data.message);
    }
  } catch (error) {
    console.error('로그인 실패:', error);
    throw error;
  }
};
```

### 2. 인증이 필요한 API 호출

```javascript
const callProtectedAPI = async (url) => {
  const token = localStorage.getItem('accessToken');
  
  if (!token) {
    throw new Error('토큰이 없습니다. 로그인이 필요합니다.');
  }
  
  try {
    const response = await fetch(url, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (response.status === 401) {
      // 토큰 만료 또는 유효하지 않음
      localStorage.removeItem('accessToken');
      localStorage.removeItem('userInfo');
      alert('토큰이 만료되었습니다. 다시 로그인해주세요.');
      // 로그인 페이지로 리다이렉트
      window.location.href = '/login';
      return;
    }
    
    const data = await response.json();
    return data;
  } catch (error) {
    console.error('API 호출 실패:', error);
    throw error;
  }
};
```

### 3. 토큰 유효성 검증

```javascript
const validateToken = async () => {
  const token = localStorage.getItem('accessToken');
  
  if (!token) {
    return false;
  }
  
  try {
    const response = await fetch('/api/auth/validate-token', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    const data = await response.json();
    return data.success;
  } catch (error) {
    console.error('토큰 검증 실패:', error);
    return false;
  }
};
```

### 4. 로그아웃 처리

```javascript
const logout = () => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('userInfo');
  window.location.href = '/';
};
```

## 에러 응답 형식

모든 API는 다음과 같은 표준 응답 형식을 사용합니다:

**성공 응답:**
```json
{
  "success": true,
  "message": "성공 메시지",
  "data": { ... }
}
```

**에러 응답:**
```json
{
  "success": false,
  "message": "에러 메시지"
}
```

## 보안 고려사항

1. **JWT 토큰 만료시간**: 2시간으로 설정되어 있습니다.
2. **로컬스토리지 사용**: 클라이언트에서 JWT를 로컬스토리지에 저장합니다.
3. **CORS 설정**: 모든 도메인에서 접근 가능하도록 설정되어 있습니다. 프로덕션에서는 특정 도메인만 허용하도록 수정하세요.
4. **비밀번호 암호화**: BCrypt를 사용하여 비밀번호를 암호화합니다.

## 개발 시 주의사항

1. JWT 시크릿 키는 프로덕션에서 더 복잡하고 안전한 값으로 변경하세요.
2. 데이터베이스 연결 정보는 환경 변수로 관리하는 것을 권장합니다.
3. HTTPS 사용을 권장합니다.
