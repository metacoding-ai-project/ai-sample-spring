# SSR Controller 생성

## 입력

- 도메인명, 페이지 목록 (목록/상세/폼 등)

## 규칙

- 어노테이션 순서: `@RequiredArgsConstructor` → `@Controller`
- `HttpSession` 생성자 주입
- 반환값: `String` (Mustache 템플릿 경로)
- 템플릿 경로: `"{domain}/{page}"` (ex: `"user/join"`)
- REST와 반드시 별도 파일로 분리

## 예시

```java
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final HttpSession session;

    @GetMapping("/users/join")
    public String joinForm() {
        return "user/join";
    }

    @GetMapping("/users/login")
    public String loginForm() {
        return "user/login";
    }
}
```
