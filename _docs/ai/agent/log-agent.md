# Log Agent

## 역할

코드 변경이 발생할 때마다, 어떤 skill이 사용되었는지 자동으로 기록한다.

## 트리거

코드 파일(`.java`, `.mustache`, `.html`, `.js`)을 생성하거나 수정한 직후 자동 실행한다.
별도 호출 없이 코드 변경이 일어나면 항상 동작한다.

## 로그 파일 규칙

- **위치**: `_docs/person/log/`
- **파일명**: `log-{yyyy-MM-dd}.md` (날짜별 1개 파일)
- 같은 날짜의 로그 파일이 이미 있으면 기존 파일에 **추가(append)** 한다
- 없으면 새로 생성한다

## 동작

코드 변경이 발생하면 아래 정보를 로그에 추가한다:

1. 변경된 파일 경로
2. 사용된 skill 이름 (skill을 사용하지 않은 경우 "-")
3. 작업 요약 (1줄)

## 로그 템플릿

```markdown
# 작업 로그 {yyyy-MM-dd}

| 시간 | 파일 | Skill | 작업 |
| ---- | ---- | ----- | ---- |
| {HH:mm} | `{파일 경로}` | {skill명 또는 -} | {작업 요약} |
```

### 예시

```markdown
# 작업 로그 2026-03-06

| 시간 | 파일 | Skill | 작업 |
| ---- | ---- | ----- | ---- |
| 14:20 | `user/UserApiController.java` | - | username 중복체크 REST API 생성 |
| 14:20 | `user/UserService.java` | - | usernameCheck 메서드 추가 |
| 14:21 | `user/UserController.java` | - | 회원가입 SSR 라우트 추가 |
| 14:21 | `templates/user/join.mustache` | ajax-skill | 중복체크 fetch 함수 생성 |
```
