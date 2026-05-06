pipeline {
    agent any

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
            steps {
                sh 'docker compose --env-file .env up -d --build --force-recreate ai backend frontend'
            }
        }

        stage('Post-Deploy Health Check') {
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
                                        curl --fail --retry 12 --retry-delay 5 --retry-all-errors "http://host.docker.internal:${FRONTEND_PORT}/"
                '''
            }
        }
    }

    post {
        success {
            echo 'Deploy Success'
        }

        failure {
            echo 'Deploy Failed'
        }
    }
}