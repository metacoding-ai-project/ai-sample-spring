# REST API Controller 생성

## 입력

- 도메인명, 엔드포인트 목록 (메서드, 경로, 파라미터)

## 규칙

- 어노테이션 순서: `@RequiredArgsConstructor` → `@RestController`
- 모든 엔드포인트 주소에 `/api` 접두사 필수
- 응답: 반드시 `Resp.ok()` / `Resp.fail()` 래퍼 사용
- SSR과 반드시 별도 파일로 분리

## 예시

```java
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @GetMapping("/api/users/username-check")
    public ResponseEntity<?> usernameCheck(@RequestParam String username) {
        boolean exists = userService.usernameCheck(username);
        return Resp.ok(exists);
    }
}
```
