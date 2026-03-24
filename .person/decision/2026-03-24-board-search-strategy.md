# 게시글 검색 기능 단계별 구현 전략

- 날짜: 2026-03-24
- 참여: 사용자 + Gemini

## 배경
사용자가 게시글 목록에서 제목과 내용을 기반으로 검색할 수 있는 기능을 제공하기 위함. 단계적 접근을 통해 초기 구현의 안정성을 확보하고, 점진적으로 사용자 경험(UX)을 개선함.

## 핵심 논의
1. **1단계: SSR 기반 기본 검색 (Form 요청)**
   - 가장 빠르고 안정적인 구현 방식.
   - URL 파라미터(`?keyword=...`)를 활용하여 상태 유지 및 공유 가능.
2. **2단계: AJAX 실시간 검색 (Event 기반)**
   - 페이지 새로고침 없이 `input` 이벤트 발생 시 서버에 요청.
   - `BoardApiController`를 통해 JSON 데이터를 받아 동적으로 DOM 업데이트.
3. **3단계: 디바운스(Debounce) 적용 (최적화)**
   - 입력이 멈춘 후 일정 시간(예: 400ms) 대기 후 최종 요청 전송.
   - 불필요한 서버 부하 감소 및 부드러운 UX 제공.

## 결론
- **기술 스택:** Mustache (SSR), JavaScript (AJAX, Debounce), Spring Boot (Controller, ApiController).
- **검색 범위:** 제목(title) 및 내용(content).
- **방향:** SSR을 우선 완성하여 기능적 요구사항을 충족한 뒤, AJAX와 디바운스를 통해 성능과 사용성을 고도화한다.

## 다음 단계
1. `BoardRepository`에 키워드 검색 쿼리 추가.
2. `BoardService` 및 `BoardController` 수정 (SSR 구현).
3. `list.mustache`에 검색 폼 추가.
4. `BoardApiController` 생성 및 AJAX/디바운스 로직 구현.
