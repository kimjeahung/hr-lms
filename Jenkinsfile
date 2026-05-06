pipeline {
    agent any

    stages {

        stage('Git Pull') {
            steps {
                git branch: 'main',
                url: 'https://github.com/wpghksdnd/hr-lms.git'
            }
        }

        stage('Docker Compose Build') {
            steps {
                sh 'docker compose down'
                sh 'docker compose up -d --build'
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