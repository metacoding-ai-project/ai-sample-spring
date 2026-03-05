# skill: java-code-convention

## 목적
이 프로젝트의 Java 소스 파일을 생성하거나 수정할 때 반드시 이 컨벤션을 따른다.

---

## 적용 시점
아래 요청 시 이 스킬을 활성화한다:
- 엔티티 / 리포지토리 / 서비스 / 컨트롤러 / 요청 DTO / 응답 DTO 생성
- 새 기능, 엔드포인트, 도메인 추가
- 기존 코드의 컨벤션 위반 검토 및 수정

---

## 패키지 구조

도메인 기반 플랫 구조를 사용한다. 레이어 기반 구조는 절대 사용하지 않는다.

```
com.example.demo/
  _core/utils/       ← 도메인 무관 공통 유틸 (Resp.java 등)
  {domain}/          ← 해당 도메인의 모든 파일을 한 폴더에 (플랫)
    {Domain}.java
    {Domain}Controller.java       ← SSR (Mustache)
    {Domain}ApiController.java    ← REST API (/api 접두사)
    {Domain}Service.java
    {Domain}Repository.java
    {Domain}Request.java
    {Domain}Response.java
```

---

## 엔티티 규칙

```java
// 어노테이션 순서: @NoArgsConstructor → @Data → @Entity → @Table
@NoArgsConstructor
@Data
@Entity
@Table(name = "{domain}_tb")   // 테이블명: {domain}_tb (snake_case + _tb 접미사)
public class {Domain} {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;         // PK 타입은 항상 Integer

    // ... 필드 ...

    // 모든 연관관계는 반드시 LAZY
    @ManyToOne(fetch = FetchType.LAZY)
    private OtherEntity other;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // @Builder는 생성자에만 선언, 클래스 레벨 금지
    @Builder
    public {Domain}(Integer id, ..., LocalDateTime createdAt) {
        this.id = id;
        // 컬렉션(List, Set) 필드는 생성자에 포함하지 않는다
    }
}
```

**반드시 지킬 것**
- `@Builder`는 생성자에만 — 클래스 레벨 선언 금지
- 모든 연관관계: `FetchType.LAZY` — EAGER 금지
- PK 타입: `Integer` (`Long` 사용 금지)
- 테이블명: `{domain}_tb`
- 생성일: `@CreationTimestamp` + `LocalDateTime createdAt`
- 컬렉션 필드(`List`, `Set`)는 `@Builder` 생성자에 포함하지 않는다

---

## 리포지토리 규칙

```java
public interface {Domain}Repository extends JpaRepository<{Domain}, Integer> {
    // JpaRepository<Entity, PK타입>
    // 필요한 경우에만 커스텀 쿼리 메서드 추가
}
```

---

## 서비스 규칙

```java
// 어노테이션 순서: @Transactional(readOnly=true) → @RequiredArgsConstructor → @Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class {Domain}Service {

    private final {Domain}Repository {domain}Repository;

    // 읽기 메서드: 추가 어노테이션 불필요 (클래스 레벨 readOnly=true 상속)
    public {Domain}Response.Detail findById(Integer id) {
        {Domain} entity = {domain}Repository.findById(id).orElseThrow(...);
        return new {Domain}Response.Detail(entity);  // DTO 변환은 여기서
    }

    // 쓰기 메서드: @Transactional 별도 선언 필수
    @Transactional
    public void save({Domain}Request.Save req) {
        ...
    }
}
```

**반드시 지킬 것**
- 클래스 레벨 `@Transactional(readOnly = true)` 항상 선언
- 쓰기 메서드(`save`, `update`, `delete`)에는 `@Transactional` 개별 선언
- DTO는 Service 안에서 생성하여 반환 — 날(raw) Entity를 Controller로 전달 금지
- Entity를 Controller로 넘기지 않는다

---

## 컨트롤러 규칙

```java
// SSR 컨트롤러 — Mustache 뷰 반환
// 어노테이션 순서: @RequiredArgsConstructor → @Controller
@RequiredArgsConstructor
@Controller
public class {Domain}Controller {

    private final {Domain}Service {domain}Service;
    private final HttpSession session;

    @GetMapping("/{domain}s")
    public String index(Model model) {
        model.addAttribute("items", {domain}Service.findAll());
        return "{domain}/index";
    }
}
```

```java
// REST 컨트롤러 — 반드시 별도 파일로 분리, 주소에 /api 접두사 사용
// 어노테이션 순서: @RequiredArgsConstructor → @RestController
@RequiredArgsConstructor
@RestController
public class {Domain}ApiController {

    private final {Domain}Service {domain}Service;

    @GetMapping("/api/{domain}s/{id}")
    public ResponseEntity<?> detail(@PathVariable Integer id) {
        return Resp.ok({domain}Service.findById(id));
    }
}
```

**반드시 지킬 것**
- SSR(`@Controller`)과 REST(`@RestController`)는 **별도 파일로 분리**한다
  - SSR: `{Domain}Controller.java`
  - REST: `{Domain}ApiController.java`
- REST 컨트롤러의 모든 엔드포인트 주소는 `/api`를 접두사로 붙인다
- Service와 함께 `HttpSession`을 생성자로 주입 (SSR 컨트롤러)
- Mustache 컨트롤러: 반환값은 `String` (템플릿 경로)
- REST 컨트롤러: `Resp.ok()` / `Resp.fail()` 사용

---

## 요청 DTO 규칙

```java
// 외부 클래스: 어노테이션 없음
public class {Domain}Request {

    @Data                          // @Data는 내부 클래스에만
    public static class Save {     // 이름 = 기능명 (Save, Update, Delete, Login ...)
        private String field1;
        private String field2;
    }

    @Data
    public static class Update {
        private String field1;
    }
}
```

**반드시 지킬 것**
- 도메인당 파일 하나: `{Domain}Request.java`
- 외부 클래스에는 어노테이션 없음
- 내부 static class 이름: 기능명 (`Save`, `Update`, `Delete`, `Login` 등)
- `@Data`는 내부 static class에만 선언

---

## 응답 DTO 규칙

```java
// 외부 클래스: 어노테이션 없음
public class {Domain}Response {

    @Data
    public static class Detail {   // 단건 조회용
        private Integer id;
        // ... 상세 화면에 필요한 필드 ...

        // 생성자에서 Entity → DTO 변환
        public Detail({Domain} entity) {
            this.id = entity.getId();
            // ...
        }
    }

    @Data
    public static class Items {    // 목록 조회용
        private Integer id;
        // ... 목록 화면에 필요한 최소 필드 ...

        public Items({Domain} entity) {
            this.id = entity.getId();
            // ...
        }
    }
}
```

**반드시 지킬 것**
- 도메인당 파일 하나: `{Domain}Response.java`
- 외부 클래스에는 어노테이션 없음
- 내부 static class 이름: 용도명 (`Detail`, `Items`, `Summary` 등)
- Entity → DTO 변환은 생성자 또는 정적 팩토리 메서드로 처리

---

## 공통 응답 (Resp)

```java
// 성공 응답
return Resp.ok(dto);                           // status 200, body = dto

// 실패 응답
return Resp.fail(HttpStatus.BAD_REQUEST, "오류 메시지");
return Resp.fail(HttpStatus.UNAUTHORIZED, "오류 메시지");
```

- 위치: `_core/utils/Resp.java`
- 모든 REST API 응답은 반드시 `Resp<T>` 래퍼 사용 — 날(raw) 반환 금지

---

## 프론트엔드 (JavaScript) 규칙

- Ajax 호출(fetch)은 반드시 `async` / `await`으로 작성한다
- DOM 요소 접근은 `document.querySelector`만 사용한다 (`getElementById` 등 금지)
- POST 요청 방식:
  - **기본**: `<form>` 태그 + `name` 속성으로 서버에 직접 제출 (페이지 이동 방식, `x-www-form-urlencoded`)
  - **예외**: Ajax가 필요한 경우(중복체크, 부분 갱신 등)에만 `fetch` 사용

---

## 네이밍 규칙

| 대상 | 컨벤션 | 예시 |
|------|--------|------|
| 클래스 | PascalCase | `BoardService` |
| 메서드 / 변수 | camelCase | `findAll`, `userId` |
| 테이블명 | snake_case + `_tb` | `board_tb` |
| 패키지 | lowercase | `board`, `_core` |
| 요청 DTO 내부 클래스 | 기능명 | `Save`, `Update` |
| 응답 DTO 내부 클래스 | 용도명 | `Detail`, `Items` |

---

## 전역 제약

| 규칙 | 값 / 강제 사항 |
|------|----------------|
| OSIV | `false` — 절대 활성화하지 않는다 |
| Fetch 전략 | 항상 `LAZY` — `EAGER` 금지 |
| 배치 사이즈 | `default_batch_fetch_size=10` |
| 인증 방식 | `HttpSession` — 별도 요청 없으면 Spring Security 사용 금지 |
| DTO 생성 위치 | Service 레이어에서만 |
| Entity 노출 | Controller에 Entity를 절대 전달하지 않는다 |
