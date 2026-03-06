# 아이디(username) 중복체크 기능

## 1. 한 줄 요약

회원가입 페이지에서 아이디 입력 후 "중복확인" 버튼을 누르면, Ajax(fetch)로 서버에 중복 여부를 확인하고 결과를 화면에 표시하는 기능이다.

## 2. 전체 흐름

```
[브라우저 (join.mustache)]
    │
    │  username 입력 + "중복확인" 클릭
    │
    ├──→ GET /api/users/username-check?username=ssar
    │
[UserApiController]
    │
    ├──→ usernameCheck("ssar")
    │
[UserService]
    │
    ├──→ findByUsername("ssar")
    │
[UserRepository]
    │
    ├──→ SELECT * FROM user_tb WHERE username = 'ssar'
    │
[H2 DB]
    │
    └──→ 결과 (Optional<User>)
         │
         ├── 존재하면 → true (중복)
         └── 없으면   → false (사용 가능)
              │
              ▼
[브라우저] ← Resp.ok(true/false) ← DOM 업데이트 (메시지 표시)
```

## 3. 실행 흐름 설명

식당 예약 확인과 비슷하다. 손님(브라우저)이 "이 이름으로 예약된 게 있나요?"라고 물으면, 안내 데스크(Controller)가 예약 담당자(Service)에게 전달하고, 예약 담당자가 예약 장부(DB)를 확인한 뒤 "이미 있습니다" 또는 "없습니다"라고 답해주는 것이다.

핵심은 **페이지 이동 없이** 결과를 확인한다는 점이다. 일반적인 form 제출은 페이지가 새로고침되지만, Ajax(fetch)를 사용하면 화면의 일부분만 바꿀 수 있다.

## 4. 실행 흐름별 코드 + 코드 설명 + 캡쳐 화면

#### Step 1. 회원가입 페이지 (join.mustache)

> 사용자가 회원가입 페이지에 접속하면 아이디, 비밀번호, 이메일 입력 폼이 보인다.

```html
<form action="/users/join" method="post">
    <div>
        <input type="text" id="username" name="username" placeholder="아이디">
        <button type="button" onclick="usernameCheck()">중복확인</button>
        <span id="username-check-result"></span>
    </div>
    <div>
        <input type="password" name="password" placeholder="비밀번호">
    </div>
    <div>
        <input type="email" name="email" placeholder="이메일">
    </div>
    <button type="submit">회원가입</button>
</form>
```

**이게 뭐야?**
- `type="button"`: 중복확인 버튼은 form 제출이 아니라 JS 함수를 호출하기 위한 버튼이다
- `onclick="usernameCheck()"`: 버튼 클릭 시 JavaScript 함수 실행
- `<span id="username-check-result">`: 중복 여부 결과가 표시될 빈 영역

![회원가입 페이지 초기 화면](images/username-check-1.png)

#### Step 2. JavaScript fetch 함수 (ajax-skill 적용)

> "중복확인" 버튼을 누르면 서버 API를 호출하고, 응답에 따라 메시지를 표시한다.

```javascript
async function usernameCheck() {
    let username = document.querySelector("#username").value;
    let resultSpan = document.querySelector("#username-check-result");

    if (!username) {
        resultSpan.textContent = "아이디를 입력해주세요.";
        return;
    }

    try {
        let response = await fetch(`/api/users/username-check?username=${username}`);
        let result = await response.json();

        if (result.body) {
            resultSpan.textContent = "이미 사용 중인 아이디입니다.";
            resultSpan.style.color = "red";
        } else {
            resultSpan.textContent = "사용 가능한 아이디입니다.";
            resultSpan.style.color = "green";
        }
    } catch (error) {
        alert("통신 오류가 발생했습니다.");
    }
}
```

**이게 뭐야?**
- `async/await`: 서버 응답을 기다리는 비동기 처리 방식. `.then()` 체이닝보다 읽기 쉽다
- `fetch()`: 브라우저에서 서버로 HTTP 요청을 보내는 함수
- `result.body`: 서버가 `Resp.ok(true/false)`로 보낸 값. `true`면 중복, `false`면 사용 가능
- `try/catch`: 네트워크 오류 등 예외 상황을 잡아서 사용자에게 알림

#### Step 3. REST API 컨트롤러 (UserApiController)

> 브라우저의 fetch 요청을 받아서 Service에 위임한다.

```java
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @GetMapping("/api/users/username-check")
    public ResponseEntity<?> usernameCheck(@RequestParam("username") String username) {
        boolean exists = userService.usernameCheck(username);
        return Resp.ok(exists);
    }
}
```

**이게 뭐야?**
- `@RestController`: JSON 응답을 반환하는 컨트롤러 (HTML이 아님)
- `@RequestParam`: URL의 `?username=xxx` 부분에서 값을 꺼낸다
- `Resp.ok(exists)`: 공통 응답 래퍼로 감싸서 `{"status":200, "msg":"성공", "body":true}` 형태로 반환

#### Step 4. 비즈니스 로직 (UserService)

> DB에서 username 존재 여부를 확인한다.

```java
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public boolean usernameCheck(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
```

**이게 뭐야?**
- `@Transactional(readOnly = true)`: 읽기 전용 트랜잭션. DB를 변경하지 않으므로 성능 최적화
- `findByUsername()`: Spring Data JPA가 메서드 이름을 보고 자동으로 SQL을 생성한다
- `.isPresent()`: Optional이 값을 가지고 있으면 `true`, 비어있으면 `false`

#### Step 5. 중복 아이디 확인 결과

> `ssar`은 이미 DB에 존재하는 아이디이므로 "이미 사용 중인 아이디입니다." 메시지가 빨간색으로 표시된다.

![중복 아이디 확인 - ssar](images/username-check-2.png)

#### Step 6. 사용 가능한 아이디 확인 결과

> `newuser`는 DB에 없는 아이디이므로 "사용 가능한 아이디입니다." 메시지가 초록색으로 표시된다.

![사용 가능한 아이디 확인 - newuser](images/username-check-3.png)

## 5. 어려운 기술 개념 설명

### async / await
JavaScript에서 서버와 통신할 때 응답이 올 때까지 시간이 걸린다. `async/await`는 "응답이 올 때까지 기다려"라는 의미다. 카페에서 주문하고 진동벨을 받는 것처럼, `await`가 진동벨 역할을 한다. 벨이 울리면(응답이 오면) 다음 코드가 실행된다.

### Optional
Java에서 DB 조회 결과가 없을 수도 있을 때 사용한다. `null`을 직접 다루면 `NullPointerException` 위험이 있지만, `Optional`은 "값이 있을 수도 있고 없을 수도 있다"를 명시적으로 표현한다. `.isPresent()`로 값 존재 여부를, `.get()`으로 값을 꺼낸다.

### @Transactional(readOnly = true)
DB 작업을 하나의 "묶음"으로 처리하는 것이 트랜잭션이다. `readOnly = true`는 "이 묶음은 읽기만 합니다"라고 선언하는 것이다. DB가 이를 알면 불필요한 잠금(lock)을 줄이고 성능을 최적화할 수 있다.

### fetch API
브라우저에서 서버로 HTTP 요청을 보내는 최신 방법이다. 예전에는 `XMLHttpRequest`를 사용했지만, `fetch`가 더 간결하고 Promise 기반이라 `async/await`와 잘 어울린다.
