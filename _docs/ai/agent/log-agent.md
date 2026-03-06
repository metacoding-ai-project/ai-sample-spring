# Log Agent

## 역할

agent 실행이 완료된 후, 수행된 작업을 기록하는 로그 파일을 생성한다.
어떤 agent든 마지막 step에서 이 log-agent를 호출하여 실행 내역을 기록한다.

## 사용법

호출하는 agent의 **마지막 step**에 아래와 같이 추가한다:

```
### Step N. log-agent

`_docs/ai/agent/log-agent.md`를 읽고, 위 Step 1~N-1의 수행 결과를 기반으로 로그 파일을 생성한다.
```

## 입력 정보

로그 작성에 필요한 정보는 아래에서 수집한다:

1. `_docs/person/task.md` — task 이름 (로그 파일명 결정에 사용)
2. 직전 agent의 실행 컨텍스트 — 사용된 skill 목록, 각 step별 수행 결과

## 로그 파일 규칙

- **위치**: `_docs/person/log/`
- **파일명**: `{task-name}-log-{yyyy-MM-dd}.md`
  - task-name: `task.md`에서 영문 키워드를 추출하여 kebab-case로 변환
  - 예: "아이디(username) 중복체크" → `username-check-log-2026-03-06.md`
- **인코딩**: UTF-8

## 실행 지시

### Step 1. task 이름 확인

`_docs/person/task.md`를 읽고 task 이름을 확인한다.
영문 키워드를 추출하여 kebab-case 파일명을 결정한다.

### Step 2. 실행 내역 수집

직전 agent의 실행 결과를 기반으로 아래 정보를 수집한다:

- 사용된 skill 목록 (실제 실행된 것만)
- 스킵된 skill과 사유
- 각 step에서 생성/수정된 파일 목록
- 각 step의 수행 요약 (1줄)
- 검증(빌드) 결과

### Step 3. 로그 파일 생성

`_docs/person/log/{task-name}-log-{date}.md` 파일을 아래 템플릿에 따라 생성한다.

## 로그 템플릿

```markdown
# {task 이름} 실행 로그

- **일시**: {yyyy-MM-dd}
- **task**: {task.md 내용}
- **agent**: {실행한 agent 이름}
- **workflow**: {참조한 workflow 파일 경로}

---

## 사용된 Skill 목록

| #  | Skill            | 상태   |
| -- | ---------------- | ------ |
| 1  | {skill-name}     | 실행   |
| 2  | {skill-name}     | 스킵   |
| .. | ...              | ...    |

---

## Step별 수행 내역

### Step 1. {skill-name}

- **파일**: `{생성/수정된 파일 경로}`
- **작업**: {수행한 작업 요약 1줄}
- **비고**: {특이사항 또는 "-"}

### Step 2. {skill-name}

- **파일**: `{생성/수정된 파일 경로}`
- **작업**: {수행한 작업 요약 1줄}
- **비고**: {특이사항 또는 "-"}

(반복)

---

## 스킵된 Skill

| Skill          | 사유                          |
| -------------- | ----------------------------- |
| {skill-name}   | {스킵 사유}                   |

> 스킵된 skill이 없으면 이 섹션은 "없음"으로 표기한다.

---

## 검증 결과

- `./gradlew build`: {성공 / 실패}
- 실패 시 에러 내용: {에러 메시지}
```
