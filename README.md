# hr-lms

## Runtime Layout

- App stack: docker-compose.yml
- Jenkins stack: docker-compose.jenkins.yml
- Local secrets/config: .env
- Shared example: .env.example

## Local Startup

1. Copy .env.example to .env and fill in real values.
2. Start Jenkins with `docker compose -f docker-compose.jenkins.yml up -d --build`.
3. Start the app stack with `docker compose --env-file .env up -d --build`.

## Jenkins Pipeline Expectations

- The pipeline requires a workspace-local .env file.
- Backend tests run in a Gradle container.
- Frontend lint/build runs in a Node container.
- Deployment succeeds only after post-deploy health checks pass.
- 젠킨스수정


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

프론트 기술스택
React

백엔드 기술스택
spring boot
mysql

ai 기술스택
flask

도커 컨네이너 서버
front
back
ai
jenkins
