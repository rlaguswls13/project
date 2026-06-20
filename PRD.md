# PRD: Activity Retrospective

## 1. 개요

Activity Retrospective는 GitHub 활동 데이터를 수집하고, 일간 및 주간 회고를 자동 생성하는 개발자용 회고 대시보드이다. 사용자는 PR, 이슈, 커밋 활동을 한 화면에서 확인하고, GitHub Copilot SDK 또는 규칙 기반 요약 엔진을 통해 업무 성과, 장애 요소, 다음 액션을 빠르게 정리할 수 있다.

## 2. 배경 및 문제 정의

개발자는 하루 또는 한 주 동안 여러 저장소, PR, 이슈, 커밋에 걸쳐 작업한다. 시간이 지나면 어떤 일을 했는지, 어떤 문제가 있었는지, 다음에 무엇을 해야 하는지 정리하는 데 비용이 발생한다.

이 제품은 GitHub 활동 기록을 회고 가능한 형태로 모으고 자동 요약하여 다음 문제를 해결한다.

- 수동으로 업무 기록을 정리하는 시간 감소
- GitHub 활동 기반의 객관적인 회고 작성 지원
- 일간 및 주간 단위의 작업 흐름 파악
- AI 요약 실패 시에도 기본 회고를 생성할 수 있는 안정성 확보

## 3. 목표

- GitHub 활동을 백엔드에 동기화하고 대시보드에 표시한다.
- 일간 회고와 주간 회고를 생성하고 최신 회고를 조회할 수 있게 한다.
- Copilot SDK 기반 요약과 규칙 기반 fallback 요약을 모두 지원한다.
- 로컬 개발 및 Azure VM 배포가 가능한 모노레포 구조를 유지한다.
- 사용자가 별도 문서 작성 없이 회고 초안을 빠르게 확보할 수 있게 한다.

## 4. 비목표

- GitHub 외 Jira, Slack, Notion 등 외부 서비스 통합은 초기 범위에 포함하지 않는다.
- 여러 사용자 계정, 조직 권한 관리, 팀 단위 멀티테넌시는 초기 범위에 포함하지 않는다.
- 회고 문장을 사용자가 직접 편집하고 저장하는 기능은 초기 범위에 포함하지 않는다.
- 모바일 앱 또는 네이티브 데스크톱 앱은 제공하지 않는다.

## 5. 대상 사용자

### 5.1 주요 사용자

- GitHub 기반으로 개발 업무를 수행하는 개인 개발자
- 매일 또는 매주 작업 내용을 정리해야 하는 개발자
- Copilot 기반 회고 자동화를 실험하려는 개발자

### 5.2 보조 사용자

- 팀원의 작업 흐름을 확인하고 싶은 기술 리더
- Azure VM 환경에 간단한 내부 도구를 배포하려는 운영자

## 6. 핵심 사용자 시나리오

### 6.1 일간 활동 확인

1. 사용자가 대시보드에 접속한다.
2. 시스템은 백엔드에서 오늘의 활동 목록을 가져온다.
3. 사용자는 PR, 이슈, 커밋 수와 상세 활동을 확인한다.

### 6.2 GitHub 활동 동기화

1. 사용자가 GitHub 동기화 버튼을 클릭한다.
2. 백엔드는 설정된 GitHub 저장소에서 활동을 수집한다.
3. 신규 활동이 저장되고 대시보드에서 확인 가능해진다.

### 6.3 일간 회고 생성

1. 사용자가 일간 회고 생성 버튼을 클릭한다.
2. 백엔드는 오늘의 활동을 기준으로 요약을 생성한다.
3. Copilot SDK가 활성화되어 있으면 AI 요약을 시도한다.
4. AI 요약을 사용할 수 없거나 실패하면 규칙 기반 fallback 요약을 생성한다.
5. 생성된 회고에는 요약, blockers, next actions, confidence, 생성 경로 정보가 포함된다.

### 6.4 주간 회고 생성

1. 사용자가 주간 보기로 이동한다.
2. 사용자가 주간 회고 생성 버튼을 클릭한다.
3. 백엔드는 이번 주 월요일부터 현재까지의 활동을 기준으로 회고를 생성한다.
4. 사용자는 최신 주간 회고와 생성 메타데이터를 확인한다.

## 7. 기능 요구사항

### 7.1 활동 수집 및 표시

- 시스템은 GitHub 활동을 PR, ISSUE, COMMIT 유형으로 저장해야 한다.
- 시스템은 활동 제목, URL, 작성자, 저장소명, 생성 시각, 원본 JSON을 저장해야 한다.
- 대시보드는 활동 목록을 최신순으로 표시해야 한다.
- 대시보드는 오늘 활동 수와 유형별 개수를 표시해야 한다.

### 7.2 GitHub 동기화

- 사용자는 프론트엔드에서 GitHub 동기화를 요청할 수 있어야 한다.
- 백엔드는 환경 변수로 설정된 GitHub owner, repo, token을 사용해야 한다.
- 동기화 API는 처리된 활동 수를 응답해야 한다.
- GitHub token이 없는 경우에도 애플리케이션이 시작 가능해야 한다.

### 7.3 회고 생성

- 사용자는 DAILY 또는 WEEKLY 기간으로 회고 생성을 요청할 수 있어야 한다.
- DAILY 회고는 설정된 타임존의 당일 00:00부터 현재까지의 활동을 기준으로 해야 한다.
- WEEKLY 회고는 설정된 타임존의 이번 주 월요일 00:00부터 현재까지의 활동을 기준으로 해야 한다.
- 생성된 회고는 period, dateKey, summary, blockers, nextActions, confidence를 포함해야 한다.
- 생성된 회고는 generationProvider, generationModel, generationDetail 메타데이터를 포함해야 한다.

### 7.4 요약 엔진

- Copilot SDK가 활성화된 경우 백엔드는 Copilot SDK sidecar에 요약 요청을 보내야 한다.
- Copilot SDK 요약 결과가 유효하면 generationProvider는 COPILOT_SDK로 기록해야 한다.
- Copilot SDK가 비활성화되었거나 실패한 경우 규칙 기반 fallback 요약을 생성해야 한다.
- fallback 결과는 사용자가 회고 패널을 비어 있는 상태로 보지 않도록 기본 정보를 제공해야 한다.

### 7.5 최신 회고 조회

- 사용자는 DAILY 또는 WEEKLY의 최신 회고를 조회할 수 있어야 한다.
- 해당 기간의 회고가 없으면 백엔드는 204 No Content를 반환해야 한다.
- 프론트엔드는 회고가 없을 때 빈 상태 메시지를 표시해야 한다.

### 7.6 설정 표시

- 설정 화면은 GitHub 저장소, GitHub token, Copilot SDK bridge, Copilot sidecar 관련 환경 변수 정보를 표시해야 한다.
- 설정값의 실제 secret 값은 화면에 노출하지 않는다.

## 8. 비기능 요구사항

### 8.1 성능

- 대시보드 초기 로딩 시 활동 목록과 최신 회고 조회는 병렬로 수행해야 한다.
- 일반적인 개인 저장소 활동량 기준으로 주요 API는 2초 이내 응답을 목표로 한다.

### 8.2 안정성

- Copilot SDK sidecar 장애가 전체 회고 생성 실패로 이어지지 않아야 한다.
- GitHub API 오류는 사용자에게 동기화 실패로 전달되어야 하며 서버 프로세스를 중단시키지 않아야 한다.
- H2 인메모리 DB는 로컬 개발 및 샘플 실행 용도로 사용한다.

### 8.3 보안

- GitHub token과 Copilot token은 환경 변수 또는 secret으로 관리해야 한다.
- 프론트엔드는 secret 값을 직접 표시하거나 클라이언트 번들에 포함하지 않아야 한다.
- 배포 환경에서는 GitHub Actions secret과 VM 환경 변수를 사용해야 한다.

### 8.4 배포 및 운영

- 백엔드, 프론트엔드, Copilot SDK sidecar는 각각 Dockerfile을 제공해야 한다.
- Azure VM 배포는 infra 스크립트와 Docker Compose 기반으로 수행할 수 있어야 한다.
- nginx는 외부 트래픽을 프론트엔드와 백엔드로 라우팅할 수 있어야 한다.

## 9. 주요 API

| Method | Path | 설명 |
| --- | --- | --- |
| GET | `/api/activities` | 활동 목록 조회 |
| POST | `/api/sync/github` | GitHub 활동 동기화 |
| POST | `/api/retrospectives/generate?period=DAILY` | 일간 회고 생성 |
| POST | `/api/retrospectives/generate?period=WEEKLY` | 주간 회고 생성 |
| GET | `/api/retrospectives/latest?period=DAILY` | 최신 일간 회고 조회 |
| GET | `/api/retrospectives/latest?period=WEEKLY` | 최신 주간 회고 조회 |

## 10. 데이터 모델 요약

### 10.1 Activity

- id
- source: PR, ISSUE, COMMIT
- title
- url
- author
- repoName
- createdAt
- rawJson

### 10.2 Retrospective

- id
- period: DAILY, WEEKLY
- dateKey
- summary
- blockers
- nextActions
- confidence
- generationProvider
- generationModel
- generationDetail
- createdAt

## 11. 성공 지표

- 사용자가 GitHub 동기화 후 오늘 활동을 확인할 수 있다.
- 사용자가 일간 회고를 1회 이상 성공적으로 생성할 수 있다.
- 사용자가 주간 회고를 1회 이상 성공적으로 생성할 수 있다.
- Copilot SDK가 비활성화된 환경에서도 fallback 회고가 생성된다.
- 배포 환경에서 프론트엔드, 백엔드, sidecar가 정상적으로 컨테이너 실행된다.

## 12. 릴리스 범위

### 12.1 MVP

- GitHub 활동 동기화
- 일간 활동 대시보드
- 일간 회고 생성 및 최신 회고 표시
- 주간 회고 생성 및 최신 회고 표시
- Copilot SDK sidecar 연동
- 규칙 기반 fallback 요약
- Docker Compose 및 Azure VM 배포 자산

### 12.2 향후 개선 후보

- 회고 편집 및 저장
- 여러 GitHub 저장소 동기화
- 사용자 인증 및 개인별 회고 분리
- PostgreSQL 등 영구 데이터베이스 전환
- 회고를 Markdown 또는 Notion으로 내보내기
- 일정 시간마다 자동 동기화 및 자동 회고 생성
- 회고 품질 평가 및 프롬프트 개선 루프

## 13. 리스크 및 대응

| 리스크 | 영향 | 대응 |
| --- | --- | --- |
| GitHub API rate limit | 활동 동기화 실패 또는 지연 | 동기화 빈도 제한, 에러 메시지 개선 |
| Copilot SDK sidecar 장애 | AI 요약 실패 | 규칙 기반 fallback 사용 |
| 인메모리 DB 데이터 유실 | 재시작 시 데이터 초기화 | 운영 환경에서 영구 DB 도입 |
| secret 설정 누락 | 동기화 또는 요약 실패 | README와 설정 화면에 필요한 환경 변수 명시 |
| 회고 품질 편차 | 사용자의 신뢰도 저하 | 프롬프트 개선, confidence 표시, 생성 경로 기록 |

## 14. 오픈 질문

- 운영 환경에서 사용할 영구 데이터베이스는 PostgreSQL로 전환할 것인가?
- GitHub 저장소는 단일 저장소만 지원할 것인가, 여러 저장소를 지원할 것인가?
- 회고 결과를 사용자가 수정할 수 있어야 하는가?
- 인증이 필요한 내부 도구로 운영할 것인가, 개인 로컬 도구로 유지할 것인가?
- 자동 회고 생성 스케줄은 백엔드 scheduler에서 처리할 것인가, GitHub Actions 또는 외부 cron으로 처리할 것인가?