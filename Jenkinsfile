pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timestamps()
    }

    parameters {
        booleanParam(name: 'K8S_ENABLED', defaultValue: false, description: 'Deploy to Kubernetes instead of Docker Compose')
        string(name: 'K8S_NAMESPACE', defaultValue: 'hr-lms', description: 'Kubernetes namespace for deployment')
    }

    environment {
        SLACK_NOTIFICATION_CHANNEL = '#jenkins-알림'
    }

    stages {

        stage('Git Pull') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-token',
                    url: 'https://github.com/wpghksdnd/hr-lms.git'
            }
        }

        stage('Prepare Environment') {
            steps {
                withCredentials([file(credentialsId: 'lms-env-file', variable: 'ENV_FILE')]) {
                    sh '''
                        set -eu
                        cp "$ENV_FILE" .env
                        sed -i 's/\r$//' .env
                        chmod 600 .env
                        docker compose --env-file .env config -q
                    '''
                }
            }
        }

        stage('Backend Tests') {
            steps {
                sh '''
                    tar -C backend -cf - . | docker run --rm -i \
                      -v "$HOME/.gradle:/home/gradle/.gradle" \
                      -w /app \
                      gradle:8.7-jdk17 \
                      /bin/sh -c 'mkdir -p /app; tar -xf - -C /app; gradle test --no-daemon'
                '''
            }
        }

        stage('Frontend Validation') {
            steps {
                sh '''
                    tar -C frontend -cf - . | docker run --rm -i \
                      -w /app \
                      node:20-alpine \
                      /bin/sh -c 'mkdir -p /app; tar -xf - -C /app; npm ci; npm run lint; npm run build'
                '''
            }
        }

        stage('Cleanup Legacy Containers') {
            steps {
                sh '''
                    for name in lms_frontend lms_backend lms_ai hr-lms-frontend-1 hr-lms-backend-1 hr-lms-ai-1; do
                      docker rm -f "$name" 2>/dev/null || true
                    done
                '''
            }
        }

        stage('Docker Deploy') {
            when {
                expression { return !params.K8S_ENABLED }
            }
            steps {
                sh 'docker compose --env-file .env up -d --build --force-recreate ai backend frontend'
            }
        }

        stage('Kubernetes Manifest Validate') {
            when {
                expression { return params.K8S_ENABLED }
            }
            steps {
                sh '''
                    set -eu
                    kubectl version --client
                    kubectl kustomize infra/k8s >/dev/null
                '''
            }
        }

        stage('Kubernetes Deploy') {
            when {
                expression { return params.K8S_ENABLED }
            }
            steps {
                sh '''
                    set -eu
                    kubectl version --client
                    kubectl cluster-info

                    K8S_NAMESPACE="${K8S_NAMESPACE:-hr-lms}"
                    IMAGE_TAG="${BUILD_NUMBER}"

                    AI_IMAGE="hr-lms-ai:${IMAGE_TAG}"
                    BACKEND_IMAGE="hr-lms-backend:${IMAGE_TAG}"
                    FRONTEND_IMAGE="hr-lms-frontend:${IMAGE_TAG}"

                    # Build local images for a local cluster (e.g. Docker Desktop Kubernetes).
                    docker build -t "$AI_IMAGE" -t hr-lms-ai:local ./ai
                    docker build -t "$BACKEND_IMAGE" -t hr-lms-backend:local ./backend

                    NEXT_PUBLIC_API_URL=$(awk -F= '/^NEXT_PUBLIC_API_URL=/{print $2}' .env | tr -d '\r')
                    [ -n "$NEXT_PUBLIC_API_URL" ] || NEXT_PUBLIC_API_URL="http://192.168.2.46:30085"
                    docker build -t "$FRONTEND_IMAGE" -t hr-lms-frontend:local --build-arg NEXT_PUBLIC_API_URL="$NEXT_PUBLIC_API_URL" ./frontend

                    SPRING_DATASOURCE_URL=$(awk -F= '/^SPRING_DATASOURCE_URL=/{print $2}' .env | tr -d '\r')
                    SPRING_DATASOURCE_USERNAME=$(awk -F= '/^SPRING_DATASOURCE_USERNAME=/{print $2}' .env | tr -d '\r')
                    SPRING_DATASOURCE_PASSWORD=$(awk -F= '/^SPRING_DATASOURCE_PASSWORD=/{print $2}' .env | tr -d '\r')
                    AI_SERVER_URL=$(awk -F= '/^AI_SERVER_URL=/{print $2}' .env | tr -d '\r')

                    [ -n "$SPRING_DATASOURCE_URL" ] || { echo "SPRING_DATASOURCE_URL is required"; exit 1; }
                    [ -n "$SPRING_DATASOURCE_USERNAME" ] || { echo "SPRING_DATASOURCE_USERNAME is required"; exit 1; }
                    [ -n "$SPRING_DATASOURCE_PASSWORD" ] || { echo "SPRING_DATASOURCE_PASSWORD is required"; exit 1; }
                    [ -n "$AI_SERVER_URL" ] || AI_SERVER_URL="http://ai:5000"

                                        kubectl apply -f infra/k8s/namespace.yaml
                    kubectl -n "$K8S_NAMESPACE" create configmap hr-lms-config \
                      --from-literal=NEXT_PUBLIC_API_URL="$NEXT_PUBLIC_API_URL" \
                      --from-literal=SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
                      --from-literal=AI_SERVER_URL="$AI_SERVER_URL" \
                      --dry-run=client -o yaml | kubectl apply -f -

                    kubectl -n "$K8S_NAMESPACE" create secret generic hr-lms-secret \
                      --from-literal=SPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME" \
                      --from-literal=SPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD" \
                      --dry-run=client -o yaml | kubectl apply -f -

                    kubectl apply -f infra/k8s/ai.yaml
                    kubectl apply -f infra/k8s/backend.yaml
                    kubectl apply -f infra/k8s/frontend.yaml

                    kubectl -n "$K8S_NAMESPACE" set image deployment/ai ai="$AI_IMAGE"
                    kubectl -n "$K8S_NAMESPACE" set image deployment/backend backend="$BACKEND_IMAGE"
                    kubectl -n "$K8S_NAMESPACE" set image deployment/frontend frontend="$FRONTEND_IMAGE"

                    kubectl -n "$K8S_NAMESPACE" rollout status deploy/ai --timeout=180s
                    kubectl -n "$K8S_NAMESPACE" rollout status deploy/backend --timeout=240s
                    kubectl -n "$K8S_NAMESPACE" rollout status deploy/frontend --timeout=240s
                '''
            }
        }

        stage('Post-Deploy Health Check') {
            when {
                expression { return !params.K8S_ENABLED }
            }
            steps {
                sh '''
                    sed -i 's/\r$//' .env

                                        AI_PORT=$(awk -F= '/^AI_HTTP_PORT=/{print $2}' .env | tr -d '\r')
                                        BACKEND_PORT=$(awk -F= '/^BACKEND_HTTP_PORT=/{print $2}' .env | tr -d '\r')
                                        FRONTEND_PORT=$(awk -F= '/^FRONTEND_HTTP_PORT=/{print $2}' .env | tr -d '\r')

                                        [ -n "$AI_PORT" ] || AI_PORT=5005
                                        [ -n "$BACKEND_PORT" ] || BACKEND_PORT=8085
                                        [ -n "$FRONTEND_PORT" ] || FRONTEND_PORT=3005

                                        FRONTEND_CID=$(docker compose --env-file .env ps -q frontend)
                                        if [ -n "$FRONTEND_CID" ]; then
                                            i=0
                                            while [ $i -lt 24 ]; do
                                                STATUS=$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}none{{end}}' "$FRONTEND_CID")
                                                if [ "$STATUS" = "healthy" ] || [ "$STATUS" = "none" ]; then
                                                    break
                                                fi
                                                i=$((i+1))
                                                sleep 5
                                            done
                                        fi

                                        curl --fail --retry 12 --retry-delay 5 --retry-all-errors "http://host.docker.internal:${AI_PORT}/health"
                                        curl --fail --retry 12 --retry-delay 5 --retry-all-errors "http://host.docker.internal:${BACKEND_PORT}/health"
                                        curl --fail --retry 12 --retry-delay 5 --retry-all-errors -o /dev/null "http://host.docker.internal:${FRONTEND_PORT}/"
                '''
            }
        }

        stage('K8s Post-Deploy Health Check') {
            when {
                expression { return params.K8S_ENABLED }
            }
            steps {
                sh '''
                    K8S_NAMESPACE="${K8S_NAMESPACE:-hr-lms}"
                    kubectl -n "$K8S_NAMESPACE" get pods -o wide
                    curl --fail --retry 12 --retry-delay 5 --retry-all-errors "http://host.docker.internal:30085/health"
                    curl --fail --retry 12 --retry-delay 5 --retry-all-errors -o /dev/null "http://host.docker.internal:30005/"
                '''
            }
        }
    }

    post {
        success {
            script {
                try {
                    slackSend(channel: "${SLACK_NOTIFICATION_CHANNEL}", color: '#41fc03',
                        message: "운영 서버에 성공적으로 배포했습니다! \n Job : <${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>")
                } catch (Exception e) {
                    echo "Deploy Success (Slack send skipped: ${e.getMessage()})"
                }
            }
        }

        failure {
            script {
                try {
                    slackSend(channel: "${SLACK_NOTIFICATION_CHANNEL}", color: '#fc0f03',
                        message: "운영 서버에 배포가 실패했습니다! \n Job : <${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>")
                } catch (Exception e) {
                    echo "Deploy Failed (Slack send skipped: ${e.getMessage()})"
                }
            }
        }
    }
}