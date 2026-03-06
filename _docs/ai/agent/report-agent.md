# Report Agent

## 역할

workflow.md를 참조하여 코드 작업을 수행한 후, 학습용 보고서를 자동 생성한다.

## 트리거

특정 문서(`.md` 파일)를 참조하여 코드를 생성/수정한 경우에 실행한다.

발동 예시:
- "workflow.md 실행해줘"
- "워크플로우 실행해"
- "hello.md 작업 진행해줘"
- "이 문서대로 구현해줘"

즉, **어떤 `.md` 파일이든 그 문서의 작업 지시에 따라 코드를 생성/수정한 경우** 발동한다.
문서와 무관한 단순 코드 수정(버그 수정, UI 변경 등)에는 발동하지 않는다.

## 출력 위치

- **폴더**: `_docs/person/report/{기능명}-report/`
- **보고서**: `report.md`
- **캡쳐 이미지**: `images/{기능명}-{순번}.png`

기능명은 참조한 문서의 제목에서 영문 키워드를 추출하여 kebab-case로 변환한다.
예: "아이디(username) 중복체크" → `username-check`

## 동작

### Step 1. 보고서 폴더 생성

`_docs/person/report/{기능명}-report/` 폴더와 `images/` 하위 폴더를 생성한다.

### Step 2. screenshot-skill로 화면 캡쳐

`_docs/ai/skill/screenshot-skill/skill.md`를 참조하여 스크린샷을 캡쳐한다.
참조한 문서의 흐름을 기반으로 시나리오를 구성한다.

```bash
python _docs/ai/skill/screenshot-skill/script/screenshot.py \
  --base-url http://localhost:8080 \
  --name {기능명} \
  --output _docs/person/report/{기능명}-report/images/ \
  --project-dir . \
  --scenarios '{시나리오 JSON}'
```

### Step 3. 보고서 작성

아래 구조로 `report.md`를 작성한다.

## 보고서 구조

### 1. 한 줄 요약

이 기능이 뭔지 한 문장으로 설명한다.

### 2. 전체 흐름

Mermaid 다이어그램(sequenceDiagram 또는 flowchart)으로 전체 실행 흐름을 그린다.

### 3. 실행 흐름 설명

쉬운 비유를 들면서 왜 이렇게 동작하는지 설명한다.
예: "식당에서 주문하는 것과 비슷합니다. 손님(브라우저)이 메뉴(URL)를 주문하면, 웨이터(Controller)가 주방(Service)에 전달하고..."

### 4. 실행 흐름별 코드 + 코드 설명 + 캡쳐 화면

흐름 순서대로 해당 코드, 설명, 스크린샷을 함께 배치한다.

```markdown
#### Step N. {흐름 단계 이름}

> {이 단계에서 무슨 일이 일어나는지 한 줄 설명}

\```java
// 해당 코드
\```

**이게 뭐야?**
- 코드 설명 (쉬운 말로)

![{캡쳐 설명}](images/{기능명}-{순번}.png)
```

### 5. 어려운 기술 개념 설명

코드에서 처음 볼 수 있는 개념들을 쉽게 풀어서 설명한다.
예: Optional, try/catch, async/await, @Transactional 등
