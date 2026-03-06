## 제목 : Ajax(fetch) 통신으로 화면을 부분 리로드하는 스킬

## 트리거 : ajax, csr, fetch 같은 키워드로 클라이언트 사이드 부분 렌더링이 필요할 때 사용

## 모델 : 자동

## 능력

Mustache 템플릿의 `<script>` 블록 안에 fetch 함수를 생성한다.
서버 API를 호출하고, 응답 결과에 따라 DOM을 직접 조작하여 화면을 부분 갱신(CSR)한다.

### 규칙

1. **async/await 필수** — `.then()` 체이닝 금지
2. **DOM 접근** — `document.querySelector`만 사용 (`getElementById` 등 금지)
3. **응답 구조** — `Resp` 래퍼 `{ status, msg, body }` 기준으로 분기
4. **에러 처리** — `try/catch`로 감싸서 오류 시 `alert()`으로 사용자에게 알림
5. **입력 검증** — 빈값 등 기본 검증은 fetch 호출 전에 처리

### 함수 구조

```javascript
async function 함수명() {
    // 1. DOM에서 입력값 수집
    let value = document.querySelector("#선택자").value;

    // 2. API 호출
    try {
        let response = await fetch(`/api/...`);
        let result = await response.json();

        // 4. 응답 분기 (result.body 기준)
        if (result.body) {
            // body가 truthy일 때 DOM 업데이트
        } else {
            // body가 falsy일 때 DOM 업데이트
        }
    } catch (error) {
        alert("통신 오류가 발생했습니다.");
    }
}
```

### 사용 예시

| 상황 | API | 응답 분기 |
| --- | --- | --- |
| 아이디 중복체크 | `GET /api/users/username-check?username=xxx` | `body=true` → 중복, `body=false` → 사용 가능 |
| 좋아요 토글 | `POST /api/boards/{id}/like` | `body` → 갱신된 좋아요 수로 DOM 업데이트 |
| 댓글 삭제 | `DELETE /api/comments/{id}` | 성공 시 해당 댓글 DOM 제거 |
