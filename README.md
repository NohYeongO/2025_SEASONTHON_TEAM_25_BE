# 2025_SEASONTHON_TEAM_25_BE

### 🛠️ 시스템 아키텍처

<img width="535" height="318" alt="스크린샷 2025-09-07 오전 4 02 34" src="https://github.com/user-attachments/assets/75188beb-8fdb-45dd-b4f2-46337ffe861a" />

### 🧩 패키지 구조

```txt
com.freedom
├── MainServerApplication.java
├── common                           # 🔧 공통 모듈
│   ├── config
│   │   ├── SecurityConfig.java
│   │   ├── OpenAIConfig.java
│   │   ├── ThymeleafConfig.java
│   │   └── WebMvcConfig.java
│   ├── dto
│   │   └── PageResponse.java
│   ├── entity
│   │   └── BaseEntity.java
│   ├── exception
│   │   ├── ErrorCode.java
│   │   ├── ErrorResponse.java
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ValidationErrorResponse.java
│   │   ├── ValidationFieldError.java
│   │   └── custom                   # 사용자 정의 예외들
│   │       ├── DuplicateEmailException.java
│   │       ├── NewsNotFoundException.java
│   │       ├── QuizNotFoundException.java
│   │       ├── TokenExpiredException.java
│   │       ├── UserNotFoundException.java
│   │       └── ... (기타 17개 예외)
│   ├── logging
│   │   ├── Loggable.java
│   │   └── LoggingAspect.java
│   ├── notification
│   │   └── DiscordWebhookClient.java
│   ├── security
│   │   ├── CustomUserDetailsService.java
│   │   ├── CustomUserPrincipal.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtProvider.java
│   │   └── TokenValidationResult.java
│   ├── time
│   │   ├── TimeConfig.java
│   │   ├── TimeProvider.java
│   │   └── infra
│   │       └── SystemTimeProvider.java
│   └── util
│       └── HashUtil.java
├── auth                             # 🔐 인증/회원 도메인
│   ├── api
│   │   ├── AuthController.java
│   │   ├── request
│   │   │   ├── LoginRequest.java
│   │   │   ├── LogoutRequest.java
│   │   │   ├── RefreshTokenRequest.java
│   │   │   └── SignUpRequest.java
│   │   └── response
│   │       ├── LoginResponse.java
│   │       ├── SignUpResponse.java
│   │       ├── TokenResponse.java
│   │       └── UserInfo.java
│   ├── application
│   │   ├── AuthFacade.java
│   │   └── dto
│   │       ├── LoginDto.java
│   │       ├── SignUpDto.java
│   │       └── TokenDto.java
│   ├── domain
│   │   ├── RefreshToken.java
│   │   ├── User.java
│   │   ├── UserRole.java
│   │   ├── UserStatus.java
│   │   └── service
│   │       ├── FindUserService.java
│   │       ├── RefreshTokenService.java
│   │       ├── SignUpUserService.java
│   │       └── ValidateUserService.java
│   └── infra
│       ├── RefreshTokenJpaRepository.java
│       └── UserJpaRepository.java
├── news                             # 📰 뉴스 도메인
│   ├── api
│   │   ├── NewsController.java
│   │   └── response
│   │       ├── NewsContentBlockResponse.java
│   │       ├── NewsDetailResponse.java
│   │       └── NewsResponse.java
│   ├── application
│   │   ├── NewsQueryAppService.java
│   │   └── dto
│   │       ├── NewsContentBlockDto.java
│   │       ├── NewsDetailDto.java
│   │       └── NewsDto.java
│   ├── domain
│   │   ├── entity
│   │   │   ├── NewsArticle.java
│   │   │   └── NewsContentBlock.java
│   │   └── service
│   │       └── FindNewsService.java
│   └── infra
│       └── repository
│           ├── NewsArticleRepository.java
│           └── NewsContentBlockRepository.java
├── quiz                             # 🧩 퀴즈 도메인
│   ├── api
│   │   ├── QuizController.java
│   │   ├── request
│   │   │   └── QuizAnswerRequest.java
│   │   └── response
│   │       ├── DailyQuizResponse.java
│   │       └── QuizResponse.java
│   ├── application
│   │   ├── QuizFacade.java
│   │   ├── QuizService.java
│   │   └── dto
│   │       ├── DailyQuizDto.java
│   │       └── UserQuizDto.java
│   ├── domain
│   │   ├── entity
│   │   │   ├── Quiz.java
│   │   │   ├── QuizDifficulty.java
│   │   │   ├── QuizType.java
│   │   │   └── UserQuiz.java
│   │   └── service
│   │       ├── CreateDailyQuizService.java
│   │       ├── FindQuizService.java
│   │       ├── FindUserQuizService.java
│   │       ├── UpdateUserQuizService.java
│   │       └── ValidateQuizAnswerService.java
│   └── infra
│       ├── QuizRepository.java
│       └── UserQuizRepository.java
├── scrap                            # 📝 스크랩 도메인
│   ├── api
│   │   ├── ScrapController.java
│   │   ├── request
│   │   │   └── QuizScrapRequest.java
│   │   └── response
│   │       ├── NewsScrapListResponse.java
│   │       ├── QuizScrapListResponse.java
│   │       └── QuizScrapResponse.java
│   ├── application
│   │   ├── QuizScrapFacade.java
│   │   ├── ScrapFacade.java
│   │   └── dto
│   │       ├── NewsScrapDto.java
│   │       └── QuizScrapDto.java
│   ├── domain
│   │   ├── entity
│   │   │   ├── NewsScrap.java
│   │   │   └── QuizScrap.java
│   │   └── service
│   │       ├── CreateNewsScrapService.java
│   │       ├── CreateQuizScrapService.java
│   │       ├── FindNewsScrapService.java
│   │       └── FindQuizScrapService.java
│   └── infra
│       ├── NewsScrapRepository.java
│       └── QuizScrapRepository.java
├── saving                           # 💰 적금 도메인
│   ├── api
│   │   ├── SavingPaymentCommandController.java
│   │   ├── SavingProductReadController.java
│   │   ├── SavingSubscriptionCommandController.java
│   │   ├── SavingSubscriptionQueryController.java
│   │   └── subscription
│   │       ├── OpenSubscriptionRequest.java
│   │       └── OpenSubscriptionResponse.java
│   ├── application
│   │   ├── AutoDebitService.java
│   │   ├── MaturitySettlementService.java
│   │   ├── SavingPaymentCommandService.java
│   │   ├── SavingProductReadService.java
│   │   ├── SavingSubscriptionCommandService.java
│   │   ├── SavingSubscriptionQueryService.java
│   │   ├── SavingsDateService.java
│   │   ├── bootstrap
│   │   │   └── FssManualBootstrapConfig.java
│   │   ├── job
│   │   │   └── FssSnapshotSyncJob.java
│   │   ├── policy
│   │   │   ├── ProductSnapshotSyncService.java
│   │   │   └── RealDayEqualsServiceMonthPolicy.java
│   │   ├── port
│   │   │   ├── SavingProductSnapshotPort.java
│   │   │   └── SavingSubscriptionPort.java
│   │   ├── read
│   │   │   ├── SavingProductDetail.java
│   │   │   ├── SavingProductListItem.java
│   │   │   └── SavingProductOptionItem.java
│   │   └── signup
│   │       ├── OpenSubscriptionCommand.java
│   │       ├── OpenSubscriptionResult.java
│   │       ├── SavingSubscriptionService.java
│   │       └── exception
│   │           ├── InvalidAutoDebitAmountForFixedException.java
│   │           ├── MissingReserveTypeSelectionException.java
│   │           ├── MissingTermSelectionException.java
│   │           ├── ProductSnapshotNotFoundException.java
│   │           ├── ProductTermNotSupportedException.java
│   │           └── ReserveTypeNotSupportedException.java
│   ├── domain
│   │   ├── RateType.java
│   │   ├── RsrvType.java
│   │   ├── SavingProductOptionSnapshot.java
│   │   ├── SavingProductSnapshot.java
│   │   ├── payment
│   │   │   ├── SavingPaymentHistory.java
│   │   │   └── SavingPaymentHistoryRepository.java
│   │   ├── policy
│   │   │   └── TickPolicy.java
│   │   ├── shapshot
│   │   │   ├── SavingProductOptionSnapshotDraft.java
│   │   │   └── SavingProductSnapshotDraft.java
│   │   └── subscription
│   │       ├── AutoDebitAmount.java
│   │       ├── SavingSubscription.java
│   │       ├── SavingSubscriptionRepository.java
│   │       ├── ServiceDates.java
│   │       ├── SubscriptionStatus.java
│   │       └── TermMonths.java
│   └── infra
│       ├── config
│       │   ├── SavingSchedulingConfig.java
│       │   └── SavingsPolicyConfig.java
│       ├── fss
│       │   ├── FssSavingApiClient.java
│       │   ├── FssSavingApiProperties.java
│       │   ├── FssSavingMapper.java
│       │   ├── FssSavingResponseDto.java
│       │   └── FssWebClientConfig.java
│       ├── payment
│       │   ├── SavingPaymentHistoryJpaAdapter.java
│       │   └── SavingPaymentHistoryJpaRepository.java
│       ├── snapshot
│       │   ├── SavingProductOptionSnapshotJpaRepository.java
│       │   ├── SavingProductSnapshotJpaRepository.java
│       │   └── SavingSubscriptionJpaRepository.java
│       └── subscription
│           ├── SavingProductSnapshotAdapter.java
│           └── SavingSubscriptionJpaAdapter.java
├── wallet                           # 💳 지갑 도메인
│   ├── api
│   │   └── WalletQueryController.java
│   ├── application
│   │   ├── SavingTransactionService.java
│   │   └── WalletService.java
│   ├── domain
│   │   ├── TransactionReasonCode.java
│   │   ├── UserWallet.java
│   │   ├── UserWalletRepository.java
│   │   ├── WalletTransaction.java
│   │   └── WalletTransactionRepository.java
│   └── infra
│       ├── UserWalletJpaAdapter.java
│       ├── UserWalletJpaRepository.java
│       ├── WalletTransactionJpaAdapter.java
│       └── WalletTransactionJpaRepository.java
├── character                        # 👤 캐릭터 도메인
│   ├── domain
│   │   └── Character.java
│   └── infra
│       └── CharacterRepository.java
├── onboarding                       # 🚀 온보딩 도메인
│   ├── api
│   │   ├── OnboardingController.java
│   │   └── dto
│   │       └── CharacterCreateRequest.java
│   └── application
│       └── OnboardingService.java
├── home                             # 🏠 홈 도메인
│   ├── api
│   │   ├── HomeController.java
│   │   └── dto
│   │       └── HomeResponse.java
│   └── application
│       └── HomeService.java
└── admin                            # 👑 관리자 도메인
    ├── api
    │   ├── AdminAuthController.java
    │   ├── AdminDashboardController.java
    │   └── dto
    │       ├── AdminAuthCheckResponse.java
    │       ├── AdminLoginRequest.java
    │       ├── AdminLoginResponse.java
    │       ├── AdminLogoutResponse.java
    │       └── DashboardStatsResponse.java
    ├── application
    │   └── AdminDashboardService.java
    ├── news                         # 관리자 뉴스 관리
    │   ├── api
    │   │   ├── AdminNewsController.java
    │   │   └── response
    │   │       ├── AdminNewsDetailResponse.java
    │   │       └── AdminNewsResponse.java
    │   ├── application
    │   │   ├── AdminNewsService.java
    │   │   ├── dto
    │   │   │   ├── AdminNewsDetailDto.java
    │   │   │   ├── AdminNewsDto.java
    │   │   │   ├── ExistingNewsDto.java
    │   │   │   └── NewsArticleDto.java
    │   │   ├── facade
    │   │   │   └── NewsFacade.java
    │   │   └── schedule
    │   │       └── NewsScheduler.java
    │   ├── domain
    │   │   ├── model
    │   │   │   ├── ProcessedBlock.java
    │   │   │   └── ProcessedNews.java
    │   │   ├── result
    │   │   │   └── NewsClassificationResult.java
    │   │   └── service
    │   │       ├── AdminNewsCommandService.java
    │   │       ├── AdminNewsQueryService.java
    │   │       ├── NewsContentProcessingService.java
    │   │       ├── NewsExistingCheckService.java
    │   │       ├── NewsPersistenceService.java
    │   │       └── NewsSyncService.java
    │   └── infra
    │       ├── client
    │       │   ├── NewsQuizGenerationClient.java
    │       │   ├── OpenAiNewsSummaryClient.java
    │       │   ├── PolicyNewsClient.java
    │       │   └── response
    │       │       ├── ClassifiedSummaryResponse.java
    │       │       ├── NewsItem.java
    │       │       └── SummaryResponse.java
    │       └── repository
    │           └── AdminNewsRepository.java
    ├── quiz                         # 관리자 퀴즈 관리
    │   ├── api
    │   │   ├── AdminQuizController.java
    │   │   ├── request
    │   │   │   └── CreateQuizRequest.java
    │   │   └── response
    │   │       ├── AdminQuizDetailResponse.java
    │   │       └── AdminQuizResponse.java
    │   └── application
    │       └── AdminQuizService.java
    └── web
        └── AdminWebController.java
```

# 📊 파이낸셜 프리덤 프로젝트 아키텍처 정리

## 🏗️ **아키텍처 패턴**

### 핵심 설계 원칙
- **도메인 주도 설계(DDD)** 적용
- **계층형 아키텍처**: `api` → `application` → `domain` → `infra`
- **클린 아키텍처** 원칙 준수
- **단일 책임 원칙**: 작은 단위의 서비스로 분리

---

## 🔧 **도메인 구성** (총 11개 도메인)

### 📋 **전체 도메인 목록**
1. **common** - 공통 모듈 (보안, 예외처리, 설정 등)
2. **auth** - 인증/회원 관리
3. **news** - 뉴스 기사 관리  
4. **quiz** - 퀴즈 시스템
5. **scrap** - 스크랩 기능
6. **saving** - 적금 상품 관리 (가장 복잡한 도메인)
7. **wallet** - 가상 지갑 시스템
8. **character** - 캐릭터 시스템
9. **onboarding** - 사용자 온보딩
10. **home** - 홈 화면
11. **admin** - 관리자 시스템 (뉴스/퀴즈 관리 포함)

### 🎯 **도메인별 복잡도 분석**

#### 🔴 **고복잡도 도메인**
- **saving**: FSS API 연동, 스케줄링, 정책 엔진, 결제 시스템
- **admin**: 뉴스/퀴즈 관리, AI 연동, 스케줄러, 외부 API 통합
- **auth**: JWT 보안, 토큰 관리, 사용자 상태 관리

#### 🟡 **중복잡도 도메인**
- **news**: 뉴스 콘텐츠 블록 처리, 검색 기능
- **quiz**: 퀴즈 생성, 답안 검증, 일일 퀴즈 시스템
- **scrap**: 뉴스/퀴즈 스크랩, 페이징 처리

#### 🟢 **저복잡도 도메인**
- **wallet**: 지갑 잔액 관리, 거래 내역
- **character**: 캐릭터 기본 정보 관리
- **onboarding**: 온보딩 플로우 관리
- **home**: 홈 화면 데이터 조합

---

## 📁 **각 도메인 내부 구조**

### 🏛️ **표준 4계층 구조**
```
domain/
├── api/           # 🌐 REST API 컨트롤러
│   ├── request/   # 요청 DTO
│   └── response/  # 응답 DTO
├── application/   # 🎯 비즈니스 로직 (Facade, Service)
│   ├── dto/       # 내부 전송 객체
│   └── service/   # 애플리케이션 서비스
├── domain/        # 🧠 핵심 도메인 (Entity, Service)
│   ├── entity/    # 도메인 엔티티
│   └── service/   # 도메인 서비스
└── infra/         # 🔌 외부 연동 (Repository, Client)
    ├── repository/# JPA 리포지토리
    └── client/    # 외부 API 클라이언트
```

### 🔍 **계층별 역할**

#### 🌐 **API 계층**
- HTTP 요청/응답 처리
- 입력값 검증 (`@Valid`)
- 권한 확인 (`@AuthenticationPrincipal`)
- AOP 로깅 (`@Loggable`)

#### 🎯 **Application 계층**
- **Facade**: 트랜잭션 경계, 여러 도메인 서비스 조합
- **Service**: 단일 유스케이스 처리
- **DTO**: 계층 간 데이터 전송

#### 🧠 **Domain 계층**
- **Entity**: 비즈니스 규칙 캡슐화
- **Service**: 순수 도메인 로직
- **Repository Interface**: 인프라 추상화

#### 🔌 **Infrastructure 계층**
- **JPA Repository**: 데이터 영속성
- **API Client**: 외부 시스템 연동
- **Configuration**: 인프라 설정

---
### ✅ 관리자 페이지

#### 서비스 모니터링 및 관리를 위한 타임리프기반 관리자페이지 구성

<img width="1510" height="812" alt="스크린샷 2025-09-07 오전 4 12 24" src="https://github.com/user-attachments/assets/e2e57cee-aaef-42c6-bb30-2e094fdc5c20" />


---
### 🚨 디스코드 활용한 오류 모니터링

#### 뉴스 수집 스케줄러 오류, 퀴즈 부족 문제 즉시 조치를 위한 디스코드 알림 서비스 구축

<img width="1173" height="837" alt="스크린샷 2025-09-07 오전 4 16 30" src="https://github.com/user-attachments/assets/18026cea-5bc2-4f38-beab-600c0be9d32a" />









