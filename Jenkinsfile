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
                        chmod 600 .env
                        docker compose --env-file .env config -q
                    '''
                }
            }
        }

        stage('Backend Tests') {
            steps {
                sh '''
                    docker run --rm \
                      -v "$PWD/backend:/app" \
                      -v "$HOME/.gradle:/home/gradle/.gradle" \
                      -w /app \
                      gradle:8.7-jdk17 \
                      gradle test --no-daemon
                '''
            }
        }

        stage('Frontend Validation') {
            steps {
                sh '''
                    docker run --rm \
                      -v "$PWD/frontend:/app" \
                      -w /app \
                      node:20-alpine \
                      sh -lc "npm ci && npm run lint && npm run build"
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
                    set -a
                    . ./.env
                    set +a

                    curl --fail --retry 12 --retry-delay 5 --retry-connrefused "http://host.docker.internal:${AI_HTTP_PORT}/health"
                    curl --fail --retry 12 --retry-delay 5 --retry-connrefused "http://host.docker.internal:${BACKEND_HTTP_PORT}/health"
                    curl --fail --retry 12 --retry-delay 5 --retry-connrefused "http://host.docker.internal:${FRONTEND_HTTP_PORT}"
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