# 아이디 중복체크 기능 구현 리포트

## 한 줄 요약

회원가입 페이지에서 "중복확인" 버튼을 누르면, 서버에 아이디가 이미 있는지 물어보고 결과를 화면에 보여주는 기능을 만들었다.

---

## 전체 흐름 (큰 그림)

```
사용자가 아이디 입력 → "중복확인" 버튼 클릭
       ↓
브라우저(JS)가 서버에 요청 보냄 (fetch)
       ↓
서버가 DB에서 해당 아이디 검색
       ↓
있으면 true / 없으면 false 응답
       ↓
브라우저가 결과를 화면에 표시
```

---

## 만든 파일 & 수정한 파일

### 1. UserApiController.java (새로 생성)

> REST API를 받는 컨트롤러

```java
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @GetMapping("/api/users/username-check")
    public ResponseEntity<?> usernameCheck(@RequestParam("username") String username) {
        boolean isDuplicate = userService.usernameCheck(username);
        return Resp.ok(isDuplicate);
    }
}
```

**이게 뭐야?**
- 브라우저가 `/api/users/username-check?username=ssar` 이런 주소로 요청하면 이 메서드가 실행된다
- `@RestController` = JSON으로 응답하는 컨트롤러 (HTML 페이지가 아니라 데이터를 돌려줌)
- `@RequestParam` = URL의 `?username=xxx` 부분에서 값을 꺼내줌
- `Resp.ok(true/false)` = `{ "status": 200, "msg": "성공", "body": true }` 형태로 응답

---

### 2. UserService.java (메서드 추가)

> 실제 중복 여부를 판단하는 비즈니스 로직

```java
public boolean usernameCheck(String username) {
    return userRepository.findByUsername(username).isPresent();
}
```

**이게 뭐야?**
- `findByUsername()` → DB에서 해당 username을 가진 유저를 찾는다
- 결과가 `Optional<User>`로 나온다 (있을 수도 있고, 없을 수도 있다는 뜻)
- `.isPresent()` → 값이 있으면 `true` (중복!), 없으면 `false` (사용 가능!)

**Optional이 뭐야?**
```
Optional = "값이 있을 수도 있고, 없을 수도 있어" 라는 상자

Optional<User> 안에 User가 있으면 → .isPresent() = true
Optional<User> 안에 아무것도 없으면 → .isPresent() = false

왜 쓰냐면? null 체크를 안전하게 하려고!
예전: if (user != null) { ... }  ← 깜빡하면 NullPointerException
지금: optional.isPresent()       ← 실수할 일이 없음
```

---

### 3. UserController.java (라우트 추가)

> 회원가입 페이지로 이동하는 SSR 라우트

```java
@GetMapping("/users/join")
public String joinForm() {
    return "user/join";
}
```

**이게 뭐야?**
- 브라우저에서 `/users/join` 주소로 들어오면 `templates/user/join.mustache` 파일을 HTML로 보여준다
- `@Controller`는 페이지(HTML)를 응답하고, `@RestController`는 데이터(JSON)를 응답한다

---

### 4. join.mustache (새로 생성)

> 회원가입 화면 + 중복체크 JS 함수

```javascript
async function usernameCheck() {
    let username = document.querySelector("#username").value;
    let msgEl = document.querySelector("#username-msg");

    if (username.trim() === "") {
        msgEl.innerText = "아이디를 입력해주세요.";
        msgEl.style.color = "red";
        return;
    }

    try {
        let response = await fetch(`/api/users/username-check?username=${username}`);
        let result = await response.json();

        if (result.body) {
            msgEl.innerText = "이미 사용중인 아이디입니다.";
            msgEl.style.color = "red";
        } else {
            msgEl.innerText = "사용 가능한 아이디입니다.";
            msgEl.style.color = "green";
        }
    } catch (error) {
        alert("통신 오류가 발생했습니다.");
    }
}
```

**핵심 개념 설명:**

| 코드 | 설명 |
|------|------|
| `async/await` | 서버 응답을 **기다렸다가** 다음 줄을 실행한다. 안 쓰면 응답이 오기 전에 다음 줄이 실행돼서 `result`가 비어있게 된다 |
| `fetch()` | 페이지 이동 없이 서버에 요청을 보내는 함수 (Ajax) |
| `response.json()` | 서버가 보낸 JSON 문자열을 JS 객체로 바꿔준다 |
| `document.querySelector()` | HTML에서 원하는 요소를 CSS 선택자로 찾는 함수 |
| `try/catch` | fetch 중 네트워크 오류 등이 나면 catch에서 잡아서 alert로 알려준다 |

---

## SSR vs REST 차이 정리

이 프로젝트에서 컨트롤러가 2개인 이유:

| | UserController (SSR) | UserApiController (REST) |
|---|---|---|
| 어노테이션 | `@Controller` | `@RestController` |
| 하는 일 | HTML 페이지를 보여줌 | JSON 데이터를 보내줌 |
| 응답 예시 | 회원가입 페이지 | `{ "body": true }` |
| 언제 씀? | 페이지 이동할 때 | 페이지 안 바꾸고 데이터만 주고받을 때 |
| URL 규칙 | `/users/join` | `/api/users/username-check` |

---

## 응답 데이터 구조

```json
// 아이디가 이미 있을 때 (중복)
{ "status": 200, "msg": "성공", "body": true }

// 아이디가 없을 때 (사용 가능)
{ "status": 200, "msg": "성공", "body": false }
```

`Resp`라는 공통 응답 래퍼를 써서 모든 API 응답의 형태를 통일한다.
JS에서는 항상 `result.body`로 실제 데이터에 접근하면 된다.
