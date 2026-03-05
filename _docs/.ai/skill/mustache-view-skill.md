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

```html
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
    // ajax-skill 참조
</script>
```
