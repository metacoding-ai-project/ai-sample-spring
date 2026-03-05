# Feature Agent — 아이디 중복체크

## 역할

`_docs/.person/workflow.md`의 "아이디 중복체크" 기능을 아래 skill 조합으로 구현한다.

## skill 조합

```
1. service-skill       → UserService에 usernameCheck 메서드 추가
2. api-controller-skill → UserApiController 생성
3. ssr-controller-skill → UserController에 회원가입 페이지 라우트 추가
4. mustache-view-skill  → templates/user/join.mustache 생성
5. ajax-skill           → usernameCheck() fetch 함수 생성
```

※ entity-skill은 스킵 (UserRepository.findByUsername이 이미 존재)

## 실행 지시

### Step 1. service-skill

`_docs/.ai/skill/service-skill.md`를 읽고 아래 작업을 수행한다:

- 파일: `user/UserService.java`
- 메서드 추가: `usernameCheck(String username)`
- `userRepository.findByUsername(username)`으로 조회 → 존재 여부 boolean 반환
- 읽기 전용 (클래스 레벨 readOnly 상속, `@Transactional` 불필요)

### Step 2. api-controller-skill

`_docs/.ai/skill/api-controller-skill.md`를 읽고 아래 작업을 수행한다:

- 파일: `user/UserApiController.java` (새로 생성)
- 엔드포인트: `GET /api/users/username-check?username=xxx`
- `userService.usernameCheck(username)` 호출
- 응답: `Resp.ok(exists)`

### Step 3. ssr-controller-skill

`_docs/.ai/skill/ssr-controller-skill.md`를 읽고 아래 작업을 수행한다:

- 파일: `user/UserController.java`
- 메서드 추가: `GET /users/join` → `return "user/join"`

### Step 4. mustache-view-skill

`_docs/.ai/skill/mustache-view-skill.md`를 읽고 아래 작업을 수행한다:

- 파일: `templates/user/join.mustache` (새로 생성)
- 회원가입 폼: username(+중복확인 버튼), password, email, 제출 버튼
- form action: `POST /users/join`
- 중복확인 버튼: `type="button"` + `onclick="usernameCheck()"`
- 결과 표시: `<span id="username-msg"></span>`

### Step 5. ajax-skill

`_docs/.ai/skill/ajax-skill.md`를 읽고 아래 작업을 수행한다:

- 위치: `join.mustache`의 `<script>` 블록 안
- 함수: `async function usernameCheck()`
- API: `GET /api/users/username-check?username=${username}`
- 응답 분기: `result.body`가 true면 "이미 사용중", false면 "사용 가능"

### Step 6. 검증

- `./gradlew build` 실행하여 컴파일 에러 없는지 확인
