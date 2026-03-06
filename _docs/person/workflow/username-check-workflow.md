# 아이디(username) 중복체크 기능

## 개요

회원가입 시 username 입력 후 중복 여부를 Ajax로 확인하는 REST API

## 흐름

```
[사용자] → username 입력 → "중복확인" 버튼 클릭
   ↓
[JS/fetch] → GET /api/users/username-check?username=xxx
   ↓
[UserApiController] → userService.usernameCheck(username) 호출
   ↓
[UserService] → userRepository.findByUsername(username)
   ↓  존재하면 → 중복 (사용 불가)
   ↓  없으면   → 사용 가능
   ↓
[응답] → Resp.ok(true/false) 또는 Resp.fail(...)
```

## 작업 목록

### 1. UserApiController 생성

- 파일: `user/UserApiController.java`
- `@RequiredArgsConstructor` → `@RestController`
- 엔드포인트: `GET /api/users/username-check?username=xxx`
- 응답: `Resp.ok()`

### 2. UserService에 메서드 추가

- 메서드: `usernameCheck(String username)`
- `findByUsername()`으로 조회 → 존재 여부 반환
- 읽기 전용이므로 `@Transactional` 별도 선언 불필요 (클래스 레벨 readOnly 상속)

### 3. UserRepository

- `findByUsername(String username)` — 이미 존재함, 추가 작업 없음

### 4. 프론트 (Mustache + JS)

#### 4-1. 회원가입 페이지 생성 (ajax-skill 사용)

- 파일: `templates/user/join.mustache`

#### 4-2. JS 함수 (ajax-skill 사용)


#### 4-3. 응답 예시

```json
// username이 이미 존재할 때
{ "status": 200, "msg": "성공", "body": true }

// username이 없을 때 (사용 가능)
{ "status": 200, "msg": "성공", "body": false }
```

#### 4-4. SSR 라우트 추가

- `UserController`에 회원가입 페이지 이동 메서드 추가

```java
@GetMapping("/users/join")
public String joinForm() {
    return "user/join";
}
```
