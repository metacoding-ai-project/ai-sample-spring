# AI Guide

## 1. 메모리에 로드할 규칙

아래 파일을 읽고 AI 메모리에 저장한다. 모든 코드 생성/수정 시 이 규칙을 따른다.

- `_docs/ai/rule/common-rule.md` — 코드 컨벤션 규칙 (패키지 구조, 어노테이션 순서, 네이밍, 설정 등)
- `_docs/ai/project-context.md` — 프로젝트 요약본
- `_docs/ai/skill/{스킬명}-skill/skill.md` — 스킬들 (제목, 트리거만 읽기)

---

## 2. 자동 실행 Agent

코드 파일(`.java`, `.mustache`, `.html`, `.js`)을 생성/수정한 직후, 아래 agent를 자동 실행한다.

- `_docs/ai/agent/log-agent.md` — 변경된 파일과 사용된 skill을 `_docs/person/log/`에 기록

---
