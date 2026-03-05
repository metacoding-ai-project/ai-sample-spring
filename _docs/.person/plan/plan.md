# Skill & Agent 생성

## Context

교육 목적으로 `_docs/.ai/skill/`에 6개 skill, `_docs/.ai/agent/`에 1개 agent를 만든다.
`_docs/.person/workflow.md`(username 중복체크)를 이 구조로 실행할 수 있게 한다.

---

## AI 도구별 기본 skill/rule 위치

| AI 도구            | 기본 위치                             |
| ------------------ | ------------------------------------- |
| Claude Code        | `.claude/skills/`, `.claude/rules/`   |
| Cursor             | `.cursor/rules/`                      |
| Gemini Code Assist | `.gemini/rules/`, `.gemini/styles/`   |
| Codex (OpenAI)     | `codex.md` (프로젝트 루트), `.codex/` |

커스텀 위치(`_docs/.ai/skill/`)를 쓰려면 각 AI의 기본 설정 파일(CLAUDE.md, .cursor/rules 등)에서 "이 경로의 파일을 읽어라"고 참조하면 된다.

---

## 생성할 파일

| #   | 경로                                      | 역할                |
| --- | ----------------------------------------- | ------------------- |
| 1   | `_docs/.ai/skill/entity-skill.md`         | Entity + Repository |
| 2   | `_docs/.ai/skill/service-skill.md`        | Service             |
| 3   | `_docs/.ai/skill/ssr-controller-skill.md` | SSR Controller      |
| 4   | `_docs/.ai/skill/api-controller-skill.md` | REST API Controller |
| 5   | `_docs/.ai/skill/mustache-view-skill.md`  | Mustache 템플릿     |
| 6   | `_docs/.ai/skill/ajax-skill.md`           | Ajax JS             |
| 7   | `_docs/.ai/agent/feature-agent.md`        | Agent               |

---

## Skill 파일 내용

### 1. entity-skill.md

```markdown
# Entity + Repository 생성

## 입력

- 도메인명, 필드 목록, 연관관계

## 규칙

### Entity

- 어노테이션 순서: `@NoArgsConstructor` → `@Data` → `@Entity` → `@Table(name = "{domain}_tb")`
- PK: `Integer` + `GenerationType.IDENTITY`
- `@Builder`는 생성자에만 선언, 클래스 레벨 금지
- 컬렉션 필드(`List`, `Set`)는 `@Builder` 생성자에 포함하지 않는다
- 모든 연관관계: `FetchType.LAZY`
- 생성일: `@CreationTimestamp` + `LocalDateTime createdAt`
- 테이블명: `{domain}_tb`

### Repository

- `JpaRepository<{Domain}, Integer>` 상속
- 필요한 경우에만 커스텀 쿼리 메서드 추가

## 예시

### Board.java

@NoArgsConstructor
@Data
@Entity
@Table(name = "board_tb")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Board(Integer id, String title, String content, User user, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdAt = createdAt;
    }

}

### BoardRepository.java

public interface BoardRepository extends JpaRepository<Board, Integer> {
}
```

### 2. service-skill.md

```markdown
# Service 생성

## 입력

- 도메인명, 메서드 목록 (읽기/쓰기 구분)

## 규칙

- 어노테이션 순서: `@Transactional(readOnly = true)` → `@RequiredArgsConstructor` → `@Service`
- 클래스 레벨 `@Transactional(readOnly = true)` 필수
- 쓰기 메서드만 `@Transactional` 개별 선언
- DTO는 Service에서 생성 → Controller로 Entity 직접 전달 금지
- Response DTO 네이밍: Max(전체), Min(최소), Detail(조인), Option(셀렉트용)

## 예시

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponse.Max findById(Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        return new UserResponse.Max(user);
    }

    public boolean usernameCheck(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    @Transactional
    public void join(UserRequest.Join req) {
        User user = User.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .email(req.getEmail())
                .build();
        userRepository.save(user);
    }

}
```

### 3. ssr-controller-skill.md

```markdown
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

### 4. api-controller-skill.md

```markdown
# REST API Controller 생성

## 입력

- 도메인명, 엔드포인트 목록 (메서드, 경로, 파라미터)

## 규칙

- 어노테이션 순서: `@RequiredArgsConstructor` → `@RestController`
- 모든 엔드포인트 주소에 `/api` 접두사 필수
- 응답: 반드시 `Resp.ok()` / `Resp.fail()` 래퍼 사용
- SSR과 반드시 별도 파일로 분리

## 예시

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

### 5. mustache-view-skill.md

```markdown
# Mustache 템플릿 생성

## 입력

- 페이지명, 폼 필드, 표시할 데이터

## 규칙

- 파일 위치: `templates/{domain}/{page}.mustache`
- POST 요청 기본: `<form>` 태그 + `name` 속성으로 제출 (페이지 이동 방식)
- Ajax가 필요한 경우만 별도 처리 (중복체크, 부분 갱신 등)
- DOM id 네이밍: 용도를 명확히 (ex: `username-msg`)

## 예시

### templates/user/join.mustache

<h1>회원가입</h1>
<form action="/users/join" method="post">
    <div>
        <input type="text" id="username" name="username" placeholder="아이디">
        <button type="button" onclick="usernameCheck()">중복확인</button>
        <span id="username-msg"></span>
    </div>
    <input type="password" name="password" placeholder="비밀번호">
    <input type="text" name="email" placeholder="이메일">
    <button type="submit">회원가입</button>
</form>

<script>
    <!-- ajax-skill 참조 -->
</script>
```

### 6. ajax-skill.md

```markdown
# Ajax (fetch) JS 코드 생성

## 입력

- API 엔드포인트, HTTP 메서드, 요청/응답 형식

## 규칙

- 반드시 `async` / `await` 사용
- DOM 접근: `document.querySelector` 만 사용 (`getElementById` 등 금지)
- 응답 구조: `{ status, msg, body }` (Resp 래퍼)
- body 값으로 분기 처리

## 예시

async function usernameCheck() {
let username = document.querySelector("#username").value;
let msgEl = document.querySelector("#username-msg");

    if (username.trim() === "") {
        msgEl.innerText = "아이디를 입력해주세요.";
        msgEl.style.color = "red";
        return;
    }

    let response = await fetch(`/api/users/username-check?username=${username}`);
    let result = await response.json();

    if (result.body) {
        msgEl.innerText = "이미 사용중인 아이디입니다.";
        msgEl.style.color = "red";
    } else {
        msgEl.innerText = "사용 가능한 아이디입니다.";
        msgEl.style.color = "green";
    }

}
```

---

## Agent 파일 내용

### `_docs/.ai/agent/feature-agent.md`

```markdown
# Feature Agent

## 역할

workflow 문서를 읽고, 필요한 skill을 판단하여 순서대로 실행한다.

## 입력

- workflow 파일 경로 (ex: `_docs/.person/workflow.md`)

## 실행 순서

1. workflow 문서를 읽고 작업 목록을 파악한다
2. 각 작업에 필요한 skill을 매핑한다:
   - Entity/Repository 작업 → `_docs/.ai/skill/entity-skill.md`
   - Service 작업 → `_docs/.ai/skill/service-skill.md`
   - SSR Controller 작업 → `_docs/.ai/skill/ssr-controller-skill.md`
   - REST API Controller 작업 → `_docs/.ai/skill/api-controller-skill.md`
   - Mustache 템플릿 작업 → `_docs/.ai/skill/mustache-view-skill.md`
   - Ajax JS 작업 → `_docs/.ai/skill/ajax-skill.md`
3. 매핑된 skill을 읽고, skill의 규칙에 따라 코드를 생성한다
4. 코드 생성 후 빌드(`./gradlew build`)로 검증한다

## 매핑 기준

| workflow 키워드               | skill                   |
| ----------------------------- | ----------------------- |
| Entity, 테이블, 모델          | entity-skill.md         |
| Service, 비즈니스 로직        | service-skill.md        |
| 페이지 이동, SSR, 뷰 라우트   | ssr-controller-skill.md |
| API, REST, 엔드포인트         | api-controller-skill.md |
| 화면, 폼, 템플릿, mustache    | mustache-view-skill.md  |
| fetch, Ajax, 중복체크, 비동기 | ajax-skill.md           |

## 예시 실행

workflow: "username 중복체크" 기능

→ Repository에 findByUsername 이미 존재 (entity-skill 스킵)
→ service-skill: UserService에 usernameCheck 메서드 추가
→ api-controller-skill: UserApiController 생성 + GET /api/users/username-check
→ ssr-controller-skill: UserController에 GET /users/join 라우트 추가
→ mustache-view-skill: templates/user/join.mustache 생성
→ ajax-skill: usernameCheck() fetch 함수 생성
→ ./gradlew build 검증
```

---

## 검증

1. 7개 파일이 모두 생성되었는지 확인
2. 각 skill의 코드 예시가 `_docs/.ai/rule/code-rule.md` 컨벤션과 일치하는지 확인
3. agent의 매핑 테이블이 workflow.md의 작업 목록을 빠짐없이 커버하는지 확인
