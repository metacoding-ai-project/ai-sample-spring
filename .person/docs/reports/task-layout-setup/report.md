# 사후 보고서: T-1.5 공통 화면 레이아웃 구성

## 1. 작업 개요
- 일시: 2026-03-16
- 작업자: Gemini CLI
- 목적: Mustache 템플릿 재사용을 위한 공통 레이아웃 구조화 및 Bootstrap 5 적용

## 2. 작업 상세
1. **공통 레이아웃 생성**
   - `src/main/resources/templates/layout/header.mustache`: Bootstrap 5 CSS, CDN 설정 및 네비게이션 바 구현.
   - `src/main/resources/templates/layout/footer.mustache`: Bootstrap 5 JS 번들 및 푸터 레이아웃 구현.
2. **페이지 레이아웃 적용**
   - `src/main/resources/templates/home.mustache`: `header`와 `footer`를 include 하도록 구조 변경.
3. **규칙 준수**
   - 파일 명명 규칙(하이픈 사용) 준수 확인 (`header.mustache`, `footer.mustache`).

## 3. 결과 확인
- 메인 페이지 접근 시 Bootstrap 스타일이 적용된 헤더와 푸터가 정상적으로 노출됨을 기대함.
- 향후 추가될 모든 Mustache 파일은 `{{> layout/header}}` 및 `{{> layout/footer}}`를 사용하여 레이아웃을 통일할 예정임.

## 4. 비고
- 네비게이션 바의 링크는 향후 구현될 `/join-form`, `/login-form` 등과 일치하도록 미리 설정함.
