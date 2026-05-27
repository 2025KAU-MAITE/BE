# MAITE Backend

> 2025 산학프로젝트 MAITE 백엔드 서버

MAITE Backend는 회의방 기반 일정 조율, 회의 관리, 시간표 관리, 실시간 채팅, AI 음성 요약, 결제 및 구독 기능을 제공하는 Spring Boot 기반 백엔드 서버입니다.

## 목차

- [프로젝트 소개](#프로젝트-소개)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [프로젝트 구조](#프로젝트-구조)
- [시스템 구성](#시스템-구성)
- [API 기능 요약](#api-기능-요약)
- [실행 방법](#실행-방법)
- [환경 변수](#환경-변수)
- [API 문서](#api-문서)
- [WebSocket 사용](#websocket-사용)
- [배포](#배포)
- [브랜치 전략](#브랜치-전략)
- [로컬 개발 시 주의사항](#로컬-개발-시-주의사항)

## 프로젝트 소개

MAITE는 사용자들이 회의방을 만들고, 회의 일정을 관리하며, 참여자들의 위치와 시간 정보를 기반으로 회의 진행을 돕는 서비스입니다.

백엔드 서버는 다음 기능을 담당합니다.

- 사용자 회원가입, 로그인, 토큰 발급
- Google 소셜 로그인
- SMS 인증번호 기반 회원 검증
- 회의방 생성, 조회, 수정, 삭제
- 회의 생성, 조회, 수정, 삭제
- 회의 참여자 주소 입력 및 주변 장소 검색
- 사용자 시간표 생성 및 조회
- WebSocket 기반 실시간 채팅
- 채팅 이미지 업로드
- 음성 파일 기반 회의 내용 변환 및 AI 요약
- 구독 결제를 위한 KakaoPay, TossPayments 연동
- Swagger 기반 API 문서 제공
- GitHub Actions 기반 EC2 자동 배포

## 주요 기능

### 1. 회원 및 인증

- 일반 회원가입
- 이메일 중복 확인
- 로그인
- Google 로그인
- JWT 기반 인증
- Access Token 재발급
- 로그아웃
- 내 정보 조회
- 회원가입용 프로필 이미지 업로드
- SMS 인증번호 발송 및 검증
- 아이디 찾기
- 비밀번호 재설정

### 2. 회의방 관리

- 내가 속한 회의방 목록 조회
- 회의방 상세 조회
- 회의방 생성
- 회의방 수정
- 회의방 삭제
- 회의방 나가기

### 3. 회의 관리

- 내가 속한 회의 목록 조회
- 특정 회의방의 회의 목록 조회
- 회의 상세 조회
- 회의 생성
- 회의 수정
- 회의 삭제
- 회의 나가기
- 회의 참여자 주소 입력
- 중심 위치 기반 주변 카페 검색
- 회의 장소 선택

### 4. 시간표 관리

- 내 시간표 조회
- 타 사용자 시간표 조회
- 시간표 생성
- 시간표 삭제

### 5. 실시간 채팅

- WebSocket/STOMP 기반 메시지 전송
- 채팅방별 메시지 브로드캐스트
- 채팅 내역 조회
- 채팅 메시지 읽음 처리
- 채팅 이미지 업로드
- AWS S3 기반 이미지 저장

### 6. AI 음성 요약

- 음성 파일 업로드
- Clova Speech 기반 음성 텍스트 변환
- OpenAI 또는 Gemini 기반 회의 내용 요약
- 요약 결과 저장
- 질문 음성 입력 후 TTS 응답 생성
- 유료 구독 사용자 대상 AI 요약 기능 제공

### 7. 결제 및 구독

- KakaoPay 결제 요청
- KakaoPay 결제 승인
- TossPayments 결제 준비
- TossPayments 결제 승인
- 결제 완료 시 사용자 구독 등록

### 8. 헬스 체크

- `/health` 엔드포인트를 통한 서버 상태 확인

## 기술 스택

### Backend

| 분류 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.4.4 |
| Build Tool | Gradle |
| ORM | Spring Data JPA |
| Database | MySQL |
| Security | Spring Security, JWT |
| OAuth | Google OAuth2 |
| API Docs | Springdoc OpenAPI, Swagger UI |
| Realtime | WebSocket, STOMP |
| File Storage | AWS S3 |
| SMS | CoolSMS |
| AI/STT/TTS | OpenAI, Gemini, Google Speech, Google Text-to-Speech, Clova Speech |
| Payment | KakaoPay, TossPayments |
| Deploy | GitHub Actions, EC2 |

## 프로젝트 구조

```text
BE
├── .github
│   └── workflows
│       └── deploy.yml
├── gradle
├── src
│   ├── main
│   │   ├── java
│   │   │   └── maite
│   │   │       └── maite
│   │   │           ├── apiPayload
│   │   │           ├── aws
│   │   │           ├── config
│   │   │           ├── domain
│   │   │           ├── repository
│   │   │           ├── security
│   │   │           ├── service
│   │   │           └── web
│   │   │               ├── controller
│   │   │               └── dto
│   │   └── resources
│   │       └── application.yml
│   └── test
├── build.gradle
├── settings.gradle
├── gradlew
└── README.md
```

## 시스템 구성

```text
Client
  |
  | REST API / WebSocket
  v
Spring Boot Backend
  |
  |-- MySQL
  |-- AWS S3
  |-- Google OAuth2
  |-- CoolSMS
  |-- OpenAI / Gemini
  |-- Clova Speech
  |-- Google Speech / TTS
  |-- KakaoPay
  |-- TossPayments
```

## API 기능 요약

### Auth API

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/auth/signup` | 회원가입 |
| POST | `/auth/signup/upload-image` | 회원가입용 프로필 이미지 업로드 |
| POST | `/auth/signup/send-code` | 회원가입 인증번호 발송 |
| POST | `/auth/signup/verify-code` | 회원가입 인증번호 검증 |
| GET | `/auth/signup/check` | 이메일 중복 확인 |
| POST | `/auth/login` | 로그인 |
| POST | `/auth/login-google` | Google 로그인 |
| POST | `/auth/find-id/send-code` | 아이디 찾기 인증번호 발송 |
| POST | `/auth/find-id/verify` | 아이디 찾기 인증번호 검증 |
| POST | `/auth/reset-password` | 비밀번호 재설정 |
| POST | `/auth/reset-password/send-code` | 비밀번호 재설정 인증번호 발송 |
| POST | `/auth/reset-password/verify` | 비밀번호 재설정 인증번호 검증 |
| POST | `/auth/refresh` | Access Token 재발급 |
| POST | `/auth/logout` | 로그아웃 |
| GET | `/auth/me` | 내 정보 조회 |
| POST | `/auth/complete-social-signup` | 소셜 회원가입 완료 |

### Room API

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/rooms` | 내가 속한 회의방 목록 조회 |
| GET | `/rooms/{roomId}` | 회의방 상세 조회 |
| POST | `/rooms` | 회의방 생성 |
| PUT | `/rooms/{roomId}` | 회의방 수정 |
| DELETE | `/rooms/{roomId}` | 회의방 삭제 |
| DELETE | `/rooms/{roomId}/me` | 회의방 나가기 |

### Meeting API

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/meetings` | 내가 속한 회의 목록 조회 |
| GET | `/meetings/rooms/{roomId}` | 회의방 기준 회의 목록 조회 |
| GET | `/meetings/{meetingId}` | 회의 상세 조회 |
| POST | `/meetings/rooms/{roomId}` | 회의 생성 |
| PUT | `/meetings/{meetingId}` | 회의 수정 |
| DELETE | `/meetings/{meetingId}` | 회의 삭제 |
| DELETE | `/meetings/{meetingId}/me` | 회의 나가기 |
| POST | `/meetings/{meetingId}/address` | 회의 참여자 주소 입력 |
| GET | `/meetings/{meetingId}/cafes` | 회의 중심 위치 기반 주변 카페 조회 |
| PATCH | `/meetings/{meetingId}/select-place` | 회의 장소 선택 |

### Timetable API

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/timetables/my` | 내 시간표 조회 |
| GET | `/api/timetables/users/{userEmail}` | 타 사용자 시간표 조회 |
| POST | `/api/timetables` | 시간표 생성 |
| DELETE | `/api/timetables` | 시간표 삭제 |

### Chat API

| Type | Endpoint | Description |
| --- | --- | --- |
| WebSocket | `/app/chat.sendMessage/{roomId}` | 채팅 메시지 전송 |
| WebSocket | `/topic/chat/{roomId}` | 채팅방 메시지 구독 |
| WebSocket | `/app/chat.markAsRead/{roomId}` | 메시지 읽음 처리 |
| POST | `/api/chats/{roomId}/images` | 채팅 이미지 업로드 |
| GET | `/api/chats/{roomId}/messages` | 채팅 내역 조회 |

### AI API

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/AI/summary` | 음성 파일 기반 회의 요약 |
| POST | `/api/AI/reply` | 음성 질문 입력 후 TTS 응답 |
| POST | `/api/AI/summary-clova` | Clova Speech 기반 음성 변환 및 요약 |

### Payment API

#### KakaoPay

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/kakao/ready` | 카카오페이 결제 요청 |
| POST | `/kakao/success` | 카카오페이 결제 승인 |
| GET | `/kakao/cancel` | 카카오페이 결제 취소 |
| GET | `/kakao/fail` | 카카오페이 결제 실패 |

#### TossPayments

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/toss/ready` | 토스페이먼츠 결제 준비 |
| POST | `/toss/confirm` | 토스페이먼츠 결제 승인 |
| GET | `/toss/success` | 토스페이먼츠 결제 성공 처리 |
| GET | `/toss/fail` | 토스페이먼츠 결제 실패 처리 |
| GET | `/toss/cancel` | 토스페이먼츠 결제 취소 처리 |

### Health Check

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/health` | 서버 상태 확인 |

## 실행 방법

### 1. Repository Clone

```bash
git clone https://github.com/2025KAU-MAITE/BE.git
cd BE
```

### 2. 실행 권한 부여

```bash
chmod +x gradlew
```

### 3. 빌드

```bash
./gradlew clean build
```

테스트를 제외하고 빌드하려면 다음 명령어를 사용할 수 있습니다.

```bash
./gradlew clean build -x test
```

### 4. 실행

```bash
java -jar build/libs/*.jar
```

또는 Gradle을 통해 실행할 수 있습니다.

```bash
./gradlew bootRun
```

## 환경 변수

`application.yml`은 환경 변수를 통해 외부 설정 값을 주입받습니다.

### Database

| 변수명 | 설명 |
| --- | --- |
| `DB_URL` | MySQL JDBC URL |
| `DB_USERNAME` | MySQL 사용자 이름 |
| `DB_PASSWORD` | MySQL 비밀번호 |

### JWT

| 변수명 | 설명 |
| --- | --- |
| `jwt_secret` | JWT Secret Key |

### Google OAuth

| 변수명 | 설명 |
| --- | --- |
| `GoogleOAuthId` | Google OAuth Client ID |
| `GoogleOAuthPassword` | Google OAuth Client Secret |
| `GOOGLE_CLIENT_ID` | Google Client ID |

### AI

| 변수명 | 설명 |
| --- | --- |
| `OpenAIKey` | OpenAI API Key |
| `GeminiKey` | Gemini API Key |
| `encodedKey` | GCP 인증 정보 Encoded Key |

### Naver

| 변수명 | 설명 |
| --- | --- |
| `NaverMapId` | Naver Map Client ID |
| `NaverMapSecret` | Naver Map Client Secret |
| `NaverId` | Naver Search Client ID |
| `NaverSecret` | Naver Search Client Secret |
| `clova_secret_key` | Clova Speech Secret Key |
| `clova_invoke_url` | Clova Speech Invoke URL |
| `naver_storage_access_key` | Naver Object Storage Access Key |
| `naver_storage_secret_key` | Naver Object Storage Secret Key |

### AWS S3

| 변수명 | 설명 |
| --- | --- |
| `CLOUD_AWS_S3_BUCKET` | S3 Bucket 이름 |
| `CLOUD_AWS_REGION` | AWS Region |
| `CLOUD_AWS_ACCESS_KEY` | AWS Access Key |
| `CLOUD_AWS_SECRET_KEY` | AWS Secret Key |

### CoolSMS

| 변수명 | 설명 |
| --- | --- |
| `coolsmsKey` | CoolSMS API Key |
| `coolsmsSecret` | CoolSMS API Secret |
| `coolsmsNum` | 발신 번호 |

### Payment

| 변수명 | 설명 |
| --- | --- |
| `admin_key` | Kakao Admin Key |
| `toss_client_key` | TossPayments Client Key |
| `toss_secret_key` | TossPayments Secret Key |

## application.yml 예시

> 실제 운영 키는 절대 GitHub에 커밋하지 말고, 로컬 `.env` 또는 배포 환경의 Secret으로 관리해야 합니다.

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.MySQL8Dialect

  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GoogleOAuthId}
            client-secret: ${GoogleOAuthPassword}

  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

  cloud:
    gcp:
      credentials:
        encoded-key: ${encodedKey}

oauth:
  google:
    client-id: ${GOOGLE_CLIENT_ID}

openai:
  api:
    key: ${OpenAIKey}

gemini:
  api:
    key: ${GeminiKey}

naver:
  map:
    client-id: ${NaverMapId}
    client-secret: ${NaverMapSecret}
  search:
    client-id: ${NaverId}
    client-secret: ${NaverSecret}

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
    path: /swagger-ui.html

jwt:
  secret: ${jwt_secret}
  expiration: 3600000
  refreshTokenValidity: 604800000

coolsms:
  api:
    key: ${coolsmsKey}
    secret: ${coolsmsSecret}
  from:
    number: ${coolsmsNum}

kakao:
  admin-key: ${admin_key}

toss:
  client-key: ${toss_client_key}
  secret-key: ${toss_secret_key}

clova:
  speech:
    secret-key: ${clova_secret_key}
    invoke-url: ${clova_invoke_url}

object-storage:
  bucket: maite
  region: kr-standard
  access-key: ${naver_storage_access_key}
  secret-key: ${naver_storage_secret_key}

cloud:
  aws:
    s3:
      bucket: ${CLOUD_AWS_S3_BUCKET}
      path:
        chat: chat
        profile: profile
    region:
      static: ${CLOUD_AWS_REGION}
    stack:
      auto: false
    credentials:
      accessKey: ${CLOUD_AWS_ACCESS_KEY}
      secretKey: ${CLOUD_AWS_SECRET_KEY}
```

## API 문서

서버 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI Docs는 다음 경로에서 확인할 수 있습니다.

```text
http://localhost:8080/v3/api-docs
```

## WebSocket 사용

WebSocket 연결 엔드포인트는 다음과 같습니다.

```text
/ws-chat
```

STOMP 메시지 전송 Prefix는 다음과 같습니다.

```text
/app
```

채팅방 구독 경로는 다음과 같습니다.

```text
/topic/chat/{roomId}
```

메시지 전송 경로는 다음과 같습니다.

```text
/app/chat.sendMessage/{roomId}
```

읽음 처리 경로는 다음과 같습니다.

```text
/app/chat.markAsRead/{roomId}
```

## 배포

이 프로젝트는 GitHub Actions를 통해 `develop` 브랜치에 Push가 발생하면 자동으로 빌드 및 배포됩니다.

배포 흐름은 다음과 같습니다.

```text
develop 브랜치 Push
  ↓
GitHub Actions 실행
  ↓
JDK 17 설정
  ↓
application.yml 생성
  ↓
Gradle Build
  ↓
JAR Artifact 업로드
  ↓
EC2 서버로 JAR 전송
  ↓
기존 Java 프로세스 종료
  ↓
새 Spring Boot 애플리케이션 실행
```

GitHub Actions에서 사용하는 주요 Secret은 다음과 같습니다.

| Secret | 설명 |
| --- | --- |
| `APPLICATION_YML` | 배포 환경용 application.yml 전체 내용 |
| `EC2_KEY` | EC2 SSH Private Key |
| `EC2_USER` | EC2 접속 사용자 |
| `EC2_HOST` | EC2 Host 주소 |

## 브랜치 전략

현재 기본 브랜치는 `develop`입니다.

```text
develop
```

기능 개발 시에는 기능별 브랜치를 생성하여 작업한 뒤, `develop` 브랜치로 병합하는 방식을 사용할 수 있습니다.

예시:

```text
feature/auth
feature/meeting
feature/chat
feature/payment
fix/login-token
```

## 로컬 개발 시 주의사항

- `application.yml`에는 민감한 값이 포함되므로 실제 키를 커밋하지 않습니다.
- MySQL 서버가 실행 중이어야 합니다.
- JWT Secret 값이 설정되어 있어야 인증 기능이 정상 동작합니다.
- Google OAuth, CoolSMS, S3, AI, 결제 API는 각각 외부 서비스 키가 필요합니다.
- WebSocket 채팅 기능을 테스트할 경우 `/ws-chat` 연결과 `/topic/chat/{roomId}` 구독 경로를 함께 확인해야 합니다.
- 파일 업로드 기능을 사용하려면 S3 또는 Object Storage 설정이 필요합니다.
- AI 요약 기능을 사용하려면 Clova Speech 및 OpenAI 또는 Gemini API 설정이 필요합니다.
- 결제 기능을 사용하려면 KakaoPay 또는 TossPayments 관련 키가 필요합니다.

## License

이 프로젝트의 라이선스 정보는 별도로 명시되어 있지 않습니다.
