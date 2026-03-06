## 제목 : 브라우저 자동 스크린샷 캡쳐 스킬

## 트리거 : report-agent에서 호출. 직접 사용하지 않음.

## 모델 : 자동

## 능력

Python Playwright로 브라우저를 자동 조작하고 스크린샷을 캡쳐한다.
서버를 자동 실행하고, 시나리오대로 페이지 접속 → 입력/클릭 → 캡쳐 → 서버 종료까지 처리한다.

### 스크립트 위치

`_docs/ai/skill/screenshot-skill/script/screenshot.py`

### 최초 1회 설치

```bash
cd _docs/ai/skill/screenshot-skill/script
pip install -r requirements.txt
playwright install chromium
```

### 실행 방법

```bash
python _docs/ai/skill/screenshot-skill/script/screenshot.py \
  --base-url http://localhost:8080 \
  --name {기능명} \
  --output _docs/person/report/{기능명}-report/images/ \
  --project-dir . \
  --scenarios '{시나리오 JSON}'
```

### 시나리오 JSON 형식

```json
[
  {
    "url": "/users/join",
    "actions": [],
    "wait": 0
  },
  {
    "url": "/users/join",
    "actions": [
      { "type": "fill", "selector": "#username", "value": "ssar" },
      { "type": "click", "selector": "button[onclick]" }
    ],
    "wait": 1000
  }
]
```

### 지원 액션

| type | 설명 | 필수 필드 |
| ---- | ---- | --------- |
| fill | 입력 필드에 값 입력 | selector, value |
| click | 요소 클릭 | selector |

### 출력

- `{output}/{name}-1.png`, `{name}-2.png`, ... 순번으로 저장
