# Entity + Repository 생성

## 입력

- 도메인명, 필드 목록, 연관관계

## 규칙

### Entity

- 어노테이션 순서: `@NoArgsConstructor` → `@Data` → `@Entity` → `@Table(name = "{domain}_tb")`
- PK: `Integer` + `GenerationType.IDENTITY`
- `@Builder`는 생성자에만 선언, 클래스 레벨 금지
- 컬렉션 필드(`List`, `Set`)는 `@Builder` 생성자에 포함하지 않는다
- 모든 연관관계: `FetchType.LAZY`
- 생성일: `@CreationTimestamp` + `LocalDateTime createdAt`
- 테이블명: `{domain}_tb`

### Repository

- `JpaRepository<{Domain}, Integer>` 상속
- 필요한 경우에만 커스텀 쿼리 메서드 추가

## 예시

### Board.java

```java
@NoArgsConstructor
@Data
@Entity
@Table(name = "board_tb")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Board(Integer id, String title, String content, User user, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdAt = createdAt;
    }
}
```

### BoardRepository.java

```java
public interface BoardRepository extends JpaRepository<Board, Integer> {
}
```
