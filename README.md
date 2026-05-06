## HR-LMS
## 실행 구성
  애플리케이션 실행 스택: docker-compose.yml
  Jenkins 실행 스택: docker-compose.jenkins.yml
  로컬 환경 변수 및 비밀 설정 파일: .env
  공유용 예시 환경 설정 파일: .env.example

## 로컬 실행 방법
  .env.example 파일을 복사하여 .env 파일 생성 후 실제 값으로 수정합니다.

  아래 명령어로 Jenkins를 실행합니다.
  docker compose -f docker-compose.jenkins.yml up -d --build

  아래 명령어로 애플리케이션 전체 스택을 실행합니다.
  docker compose --env-file .env up -d --build

## Jenkins 파이프라인 동작 조건(SCM POOL)
  Jenkins 파이프라인 실행 시 작업 공간(workspace)에 .env 파일이 존재해야 합니다.
  백엔드 테스트는 Gradle 컨테이너 내부에서 실행됩니다.
  프론트엔드 lint 및 build 작업은 Node 컨테이너 내부에서 실행됩니다.
  배포 후 Health Check가 정상적으로 통과해야 최종 배포 성공으로 처리됩니다.

## 프론트
  http://192.168.2.46:3005

## 백엔드
  http://192.168.2.46:8085

## AI
  http://192.168.2.46:5005

## 젠킨스
  http://192.168.2.46:8090/
  id: user
  pw: 1234

## mysql
  192.168.2.46
  ID: lms_user
  PW: 1234

Kubernetes(도커 컨테이너 운영관리용)
jenkins(5분마다 들어온 push 빌드) -> slack(알림)  -> 운영채널이라서 즉시 반영되도 상관없음

## front Tech stack(프론트가 기재)
  React
  tailwind

## front Tech stack
  spring boot
  mysql

## AI Tech stack
flask

## Docker container list
  front
  back
  ai
  jenkins
