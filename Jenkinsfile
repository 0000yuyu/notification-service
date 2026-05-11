pipeline {
    agent any

    tools {
        jdk 'JDK17'
    }

    environment {
        DOCKER_IMAGE = 'sunsik17/tripmate-notification'
        DOCKER_TAG = 'latest'
        CONTAINER_NAME = 'notification-service'
        NOTIFICATION_EC2_IP = '172.31.35.239'
        PEM_PATH = '/var/lib/jenkins/tripmate.pem'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'github-token',
                    usernameVariable: 'GITHUB_USERNAME',
                    passwordVariable: 'GITHUB_TOKEN'
                )]) {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean build -x test'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-account',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    sh """
                        docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                    """
                }
            }
        }

        stage('Deploy') {
            steps {
                sh """
                    ssh -i ${PEM_PATH} -o StrictHostKeyChecking=no ec2-user@${NOTIFICATION_EC2_IP} '
                        docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker stop notification-service || true
                        docker rm notification-service || true
                        docker run -d \\
                            --name notification-service \\
                            --env-file /home/ec2-user/.env \\
                            -p 8080:8080 \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    '
                """
            }
        }
    }

    post {
      always {
          cleanWs()
          sh 'docker system prune -f'
      }
      success {
          echo 'Deploy succeeded'
      }
      failure {
          echo 'Deploy failed'
      }
    }
}