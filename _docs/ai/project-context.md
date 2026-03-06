# Project Context

## 프로젝트 개요

Spring Boot 기반의 **블로그 만들기** 프로젝트이다.
사용자가 회원가입/로그인 후 게시글을 작성하고, 댓글을 달 수 있는 블로그 서비스를 구현한다.

---

## 아키텍처

### 전체 구조

```
Client (Browser)
    │
    ├── SSR 요청 ──→ @Controller ──→ Service ──→ Repository ──→ H2 DB
    │                    │
    │                    └──→ Mustache Template ──→ HTML 응답
    │
    └── API 요청 ──→ @RestController ──→ Service ──→ Repository ──→ H2 DB
                         │
                         └──→ Resp<T> JSON 응답
```

- **SSR (Server-Side Rendering)**: Mustache 템플릿 엔진을 사용한 페이지 렌더링
- **REST API**: `/api` 접두사, `Resp<T>` 공통 응답 래퍼 사용
- **인증**: HttpSession 기반 (Spring Security 미사용)

### 패키지 구조 (도메인 기반 플랫 구조)

```
com.example.demo/
  _core/
    utils/
      Resp.java              ← 공통 API 응답 래퍼
  user/
    User.java                ← 회원 엔티티
    UserController.java      ← SSR 컨트롤러
    UserService.java         ← 비즈니스 로직
    UserRepository.java      ← 데이터 접근
    UserRequest.java         ← 요청 DTO
    UserResponse.java        ← 응답 DTO
  board/
    Board.java               ← 게시글 엔티티
    BoardController.java     ← SSR 컨트롤러
    BoardService.java        ← 비즈니스 로직
    BoardRepository.java     ← 데이터 접근
    BoardRequest.java        ← 요청 DTO
    BoardResponse.java       ← 응답 DTO
  reply/
    Reply.java               ← 댓글 엔티티
    ReplyController.java     ← SSR 컨트롤러
    ReplyService.java        ← 비즈니스 로직
    ReplyRepository.java     ← 데이터 접근
    ReplyRequest.java        ← 요청 DTO
    ReplyResponse.java       ← 응답 DTO
```

---

## 기술 스택

| 분류       | 기술                                  |
| ---------- | ------------------------------------- |
| Language   | Java 21                              |
| Framework  | Spring Boot 3.3.4                     |
| Build Tool | Gradle                                |
| ORM        | Spring Data JPA (Hibernate)           |
| DB         | H2 (In-Memory)                        |
| Template   | Mustache                              |
| Auth       | HttpSession                           |
| Library    | Lombok                                |
| Test       | JUnit 5 (Spring Boot Starter Test)    |
| Dev Tool   | Spring Boot DevTools                  |

---

## 도메인 모델 (ERD)

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│   user_tb    │       │   board_tb   │       │   reply_tb   │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id (PK)      │──┐    │ id (PK)      │──┐    │ id (PK)      │
│ username (UQ)│  │    │ title        │  │    │ comment      │
│ password     │  ├───<│ content      │  ├───<│ user_id (FK) │
│ email        │  │    │ user_id (FK) │  │    │ board_id (FK)│
│ created_at   │  │    │ created_at   │  │    │ created_at   │
└──────────────┘  │    └──────────────┘  │    └──────────────┘
                  │                      │
                  └──────────────────────┘
```

- **User → Board**: 1:N (한 유저가 여러 게시글 작성)
- **User → Reply**: 1:N (한 유저가 여러 댓글 작성)
- **Board → Reply**: 1:N (한 게시글에 여러 댓글)
- 모든 연관관계는 `FetchType.LAZY`

---

## 주요 설정

| 항목                   | 값                         |
| ---------------------- | -------------------------- |
| Server Port            | 8080                       |
| OSIV                   | false                      |
| Batch Fetch Size       | 10                         |
| DB URL                 | jdbc:h2:mem:test           |
| H2 Console             | 활성화 (/h2-console)       |
| SQL 초기화             | classpath:db/data.sql      |
| Character Encoding     | UTF-8 (강제)               |

---

## 핵심 기능 (예정 포함)

| 도메인 | 기능                                      | 상태     |
| ------ | ----------------------------------------- | -------- |
| User   | 회원가입, 로그인, 로그아웃, 중복체크       | 개발 중  |
| Board  | 게시글 CRUD, 목록 조회, 상세 조회          | 개발 중  |
| Reply  | 댓글 작성, 삭제                            | 개발 중  |

---

## 컨벤션 참조

코드 작성 시 반드시 아래 문서의 규칙을 따른다.

- [코드 컨벤션](_docs/ai/rule/common-rule.md)
