# Ajax (fetch) JS 코드 생성

## 입력

- API 엔드포인트, HTTP 메서드, 요청/응답 형식

## 규칙

- 반드시 `async` / `await` 사용
- DOM 접근: `document.querySelector` 만 사용 (`getElementById` 등 금지)
- 응답 구조: `{ status, msg, body }` (Resp 래퍼)
- body 값으로 분기 처리

## 예시

```javascript
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
