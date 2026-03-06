# AI Guide

> 개발자의 역할은 코드를 직접 짜는 것이 아니라, **workflow를 잘 설계하는 것**이다.
> 명확한 workflow를 작성하고 AI에게 지시하는 것 — 이것이 앞으로의 개발 방식이다.
> 코드는 AI가 만든다. 개발자는 **무엇을, 왜, 어떤 순서로** 만들지를 정의한다.

---

## 1. 메모리에 로드할 규칙

아래 파일을 읽고 AI 메모리에 저장한다. 모든 코드 생성/수정 시 이 규칙을 따른다.

- `_docs/ai/rule/common-rule.md` — 코드 컨벤션 규칙 (패키지 구조, 어노테이션 순서, 네이밍, 설정 등)
- `_docs/ai/project-context.md` — 프로젝트 요약본
- `_docs/ai/skill/{스킬명}-skill/skill.md` — 스킬들 (제목, 트리거만 읽기)

---

## 2. 자동 실행 Agent

코드 파일(`.java`, `.mustache`, `.html`, `.js`)을 생성/수정한 직후, 아래 agent를 자동 실행한다.

- `_docs/ai/agent/log-agent.md` — 변경된 파일과 사용된 skill/agent를 `_docs/person/log/{날짜}/`에 기록

문서(`.md`)를 참조하여 코드 작업을 수행한 후, 아래 agent를 자동 실행한다.

- `_docs/ai/agent/report-agent.md` — 문서 기반 코드 작업 완료 후 보고서 자동 생성

---

## 3. 문서 생성 규칙

workflow 문서 생성 요청 시 아래 규칙을 따른다.

- **파일명**: `{기능명}-workflow.md` (영문 kebab-case)
- **위치**: `_docs/person/workflow/`
- **예시**: `username-check-workflow.md`, `board-crud-workflow.md`

---
