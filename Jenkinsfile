pipeline {
    agent any

    tools {
        jdk 'JDK17'
    }

    environment {
        DOCKER_IMAGE = 'yujsong/tripmate-notification-service'
        DOCKER_TAG = 'latest'
        CONTAINER_NAME = 'notification-service'
        TARGET_SERVER_IP = '10.0.0.10'
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
                        credentialsId: 'docker-token',
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
                sshagent(credentials: ['gcp-ssh-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no g0000yuyu510@${TARGET_SERVER_IP} "
                            docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                            docker stop ${CONTAINER_NAME} || true
                            docker rm ${CONTAINER_NAME} || true
                            docker run -d \
                                --name ${CONTAINER_NAME} \
                                --network host \
                                --env-file /home/g0000yuyu510/.env \
                                -e SPRING_PROFILES_ACTIVE=test \
                                -e EUREKA_INSTANCE_PREFER_IP_ADDRESS=true \
                                -e EUREKA_INSTANCE_NON_SECURE_PORT='\\\${server.port}' \
                                -e EUREKA_INSTANCE_INSTANCE_ID='\\\${spring.application.name}:\\\${server.port}:\\\${random.value}' \
                                --restart always \
                                ${DOCKER_IMAGE}:${DOCKER_TAG}
                        "
                    """
                }
            }
        }
    }

    post {
        always {
            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                cleanWs()
            }
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
