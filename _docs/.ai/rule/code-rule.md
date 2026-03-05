# 코드 컨벤션

이 프로젝트의 소스 파일을 생성하거나 수정할 때 반드시 이 컨벤션을 따른다.

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

## 어노테이션 순서

| 레이어         | 순서                                                                         |
| -------------- | ---------------------------------------------------------------------------- |
| Entity         | `@NoArgsConstructor` → `@Data` → `@Entity` → `@Table(name = "{도메인}_tb")` |
| Service        | `@Transactional(readOnly = true)` → `@RequiredArgsConstructor` → `@Service`  |
| Controller     | `@RequiredArgsConstructor` → `@Controller`                                   |
| RestController | `@RequiredArgsConstructor` → `@RestController` (별도 파일, `/api` 접두사)    |

---

## Entity 규칙

```java
@NoArgsConstructor
@Data
@Entity
@Table(name = "{domain}_tb")
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
- PK 타입: `Integer` (`Long` 사용 금지), 전략: `GenerationType.IDENTITY`
- `@Builder`는 생성자에만 — 클래스 레벨 선언 금지
- 컬렉션 필드(`List`, `Set`)는 `@Builder` 생성자에 포함하지 않는다
- 모든 연관관계: `FetchType.LAZY` — EAGER 금지
- 생성일: `@CreationTimestamp` + `LocalDateTime createdAt`
- 테이블명: `{domain}_tb`

---

## Service 규칙

```java
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class {Domain}Service {

    private final {Domain}Repository {domain}Repository;

    // 읽기 메서드: 추가 어노테이션 불필요 (클래스 레벨 readOnly=true 상속)
    public {Domain}Response.Max findById(Integer id) {
        {Domain} entity = {domain}Repository.findById(id).orElseThrow(...);
        return new {Domain}Response.Max(entity);  // DTO 변환은 여기서
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

---

## Controller 규칙

```java
// SSR 컨트롤러 — Mustache 뷰 반환
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
- SSR(`@Controller`)과 REST(`@RestController`)는 **별도 파일로 분리**
- REST 엔드포인트 주소는 `/api` 접두사 필수
- SSR: `HttpSession` 생성자 주입, 반환값 `String` (템플릿 경로)
- REST: `Resp.ok()` / `Resp.fail()` 사용

---

## DTO 규칙

### 요청 DTO

```java
// 외부 클래스: 어노테이션 없음
public class {Domain}Request {

    @Data                          // @Data는 내부 클래스에만
    public static class Save {     // 이름 = 기능명 (Save, Update, Login, Join ...)
        private String field1;
        private String field2;
    }

    @Data
    public static class Update {
        private String field1;
    }
}
```

### 응답 DTO

```java
// 외부 클래스: 어노테이션 없음
public class {Domain}Response {

    @Data
    public static class Max {      // 테이블 전체 컬럼 (상세·목록 겸용)
        private Integer id;
        private String title;
        private String content;
        private LocalDateTime createdAt;

        public Max({Domain} entity) {
            this.id = entity.getId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.createdAt = entity.getCreatedAt();
        }
    }

    @Data
    public static class Min {      // 최소 정보 (id + 대표값, 세션 저장 등)
        private Integer id;
        private String title;

        public Min({Domain} entity) {
            this.id = entity.getId();
            this.title = entity.getTitle();
        }
    }

    @Data
    public static class Detail {   // 조인 포함 확장 정보
        private Integer id;
        private String title;
        private String content;
        private String username;   // 조인된 User 정보

        public Detail({Domain} entity) {
            this.id = entity.getId();
            this.title = entity.getTitle();
            this.content = entity.getContent();
            this.username = entity.getUser().getUsername();
        }
    }

    @Data
    public static class Option {   // 셀렉트박스/드롭다운용
        private Integer id;
        private String name;

        public Option({Domain} entity) {
            this.id = entity.getId();
            this.name = entity.getTitle();
        }
    }
}
```

**반드시 지킬 것**
- 도메인당 파일 하나씩: `{Domain}Request.java`, `{Domain}Response.java`
- 외부 클래스에는 어노테이션 없음 / `@Data`는 내부 static class에만
- Request 내부 클래스 이름: 기능명 (`Save`, `Update`, `Login`, `Join`)
- Response 내부 클래스 이름: 데이터 범위 기준
  - `Max`: 테이블 전체 컬럼 (상세·목록 겸용)
  - `Min`: 최소 정보 (id + 대표값)
  - `Detail`: 조인 포함 확장 정보
  - `Option`: 셀렉트박스/드롭다운용
- Entity → DTO 변환은 생성자 또는 정적 팩토리 메서드로 처리

---

## 공통 응답

```java
return Resp.ok(dto);                                    // 성공: status 200
return Resp.fail(HttpStatus.BAD_REQUEST, "오류 메시지");  // 실패
```

- 위치: `_core/utils/Resp.java`
- 모든 REST API 응답은 반드시 `Resp<T>` 래퍼 사용 — 날(raw) 반환 금지

---

## 프론트엔드 (JavaScript) 규칙

- Ajax(fetch)는 `async` / `await` 사용
- DOM 접근: `document.querySelector` 사용 (`getElementById` 등 금지)
- POST 요청 기본: `<form>` 태그 + `name` 속성으로 제출 (페이지 이동 방식)
- Ajax가 필요한 경우만 fetch 사용 (중복체크, 부분 갱신 등)

---

## 네이밍

| 대상                  | 컨벤션       | 예시                             |
| --------------------- | ------------ | -------------------------------- |
| 클래스/파일           | PascalCase   | `BoardService`                   |
| 메서드/변수           | camelCase    | `findAll`                        |
| 테이블                | snake_case + `_tb` | `board_tb`                 |
| 패키지                | lowercase    | `board`, `_core`                 |
| Request 내부 클래스   | 기능명       | `Save`, `Update`, `Login`        |
| Response 내부 클래스  | 데이터 범위  | `Max`, `Min`, `Detail`, `Option` |

---

## 설정

| 규칙         | 값 / 강제 사항                                       |
| ------------ | ---------------------------------------------------- |
| OSIV         | `false` — 절대 활성화하지 않는다                     |
| Fetch 전략   | 항상 `LAZY` — `EAGER` 금지                           |
| 배치 사이즈  | `default_batch_fetch_size=10`                         |
| 인증 방식    | `HttpSession` — 별도 요청 없으면 Spring Security 금지 |
| DTO 생성     | Service 레이어에서만                                  |
| Entity 노출  | Controller에 Entity를 절대 전달하지 않는다            |
