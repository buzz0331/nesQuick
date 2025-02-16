# 🏆 NesQuick - 네트워크 기반 멀티플레이 퀴즈 게임

## 📌 프로젝트 소개
**NesQuick**은 네트워크를 이용한 **멀티플레이 퀴즈 게임**입니다.  
사용자는 다양한 **퀴즈 모드**에서 경쟁하고 협력하며, 직접 **퀴즈를 제작**하여 공유할 수 있습니다.  
클라이언트와 서버 간 **TCP 소켓 통신**을 기반으로 설계되었으며, 실시간 상호작용을 지원합니다.

---

## 📜 목차
1. [기능 소개](#-기능-소개)
2. [프로그램 구조](#-프로그램-구조)
3. [기술 스택](#-기술-스택)
4. [실행 방법](#-실행-방법)
5. [팀원 소개](#-팀원-소개)

---

## 🎮 기능 소개
### 1️⃣ 회원가입 및 로그인
- 회원가입 후 로그인 가능
- 비밀번호 인증 및 데이터베이스 연동

### 2️⃣ 다양한 퀴즈 모드 지원
- **스피드 퀴즈 모드**: 빠르게 정답을 맞히는 플레이어가 승리
- **대전 모드**: 각자 정답을 입력하고 점수를 비교하여 승패 결정
- **협동 모드**: 플레이어들이 정보를 공유하며 문제를 해결

### 3️⃣ 퀴즈 커스터마이징
- 사용자가 직접 **퀴즈를 생성**하고, 공유할 수 있음
- 객관식, 주관식, 이미지 기반 문제 등 다양한 문제 유형 지원

### 4️⃣ 방 생성 및 실시간 채팅
- **게임 방을 생성**하고 플레이어 초대 가능
- **채팅 기능**을 통해 실시간 커뮤니케이션 지원

### 5️⃣ 랭킹 시스템
- 퀴즈 성적을 기반으로 **랭킹 표시**
- 퀴즈 생성자 평가 시스템

---

## 🏗 프로그램 구조

### 🔹 클라이언트 구조
- `QuizClient`: 서버와 통신하며, UI를 관리
- `MessageReceiver`: 서버 응답을 수신하여 처리
- `Thread`: 비동기적으로 서버 메시지를 처리하는 스레드 실행

### 🔹 서버 구조
- `QuizServer`: 클라이언트 연결을 관리하고 게임 상태를 유지
- `ClientHandler`: 각 클라이언트의 요청을 개별적으로 처리
- `MessageHandler`: 게임 모드 및 방 관리 역할 수행

---

## 🛠 기술 스택

| 분야 | 기술 |
|------|------|
| **언어** | Java |
| **프레임워크** | Java Swing (UI) |
| **네트워크** | TCP 소켓 프로그래밍 |
| **데이터베이스** | SQLite |
| **버전 관리** | GitHub |
| **협업 도구** | Notion, Discord |

---

## 🚀 실행 방법
### 1️⃣ 서버 실행
```sh
javac QuizServer.java
java QuizServer
```

### 2️⃣ 클라이언트 실행
```sh
javac QuizClient.java
java QuizClient
```

## 👨‍💻 팀원 소개

| 이름 | 역할 | GitHub ID |
|------|------|-----------|
| 장현준 | 서버 설계 및 네트워크 개발 | [<img src="https://avatars.githubusercontent.com/buzz0331" width="130" height="130">](https://github.com/buzz0331) |
| 조용찬 | 클라이언트 UI 개발 및 게임 로직 구현 | [<img src="https://avatars.githubusercontent.com/YongChanCho" width="130" height="130">](https://github.com/YongChanCho) |
| 최현우 | 퀴즈 시스템 및 데이터베이스 관리 | [<img src="https://avatars.githubusercontent.com/wohyunchoi" width="130" height="130">](https://github.com/wohyunchoi) |
| 임지예 | 채팅 및 방 관리 기능 개발 | [<img src="https://avatars.githubusercontent.com/ljyljy020202" width="130" height="130">](https://github.com/ljyljy020202) |
