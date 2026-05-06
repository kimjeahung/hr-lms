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