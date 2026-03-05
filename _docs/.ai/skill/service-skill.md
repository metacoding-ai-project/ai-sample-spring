# Service 생성

## 입력

- 도메인명, 메서드 목록 (읽기/쓰기 구분)

## 규칙

- 어노테이션 순서: `@Transactional(readOnly = true)` → `@RequiredArgsConstructor` → `@Service`
- 클래스 레벨 `@Transactional(readOnly = true)` 필수
- 쓰기 메서드만 `@Transactional` 개별 선언
- DTO는 Service에서 생성 → Controller로 Entity 직접 전달 금지
- Response DTO 네이밍: Max(전체), Min(최소), Detail(조인), Option(셀렉트용)

## 예시

```java
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponse.Max findById(Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        return new UserResponse.Max(user);
    }

    public boolean usernameCheck(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    @Transactional
    public void join(UserRequest.Join req) {
        User user = User.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .email(req.getEmail())
                .build();
        userRepository.save(user);
    }
}
```
