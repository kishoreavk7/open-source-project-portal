// =========================================================================
// Project #91 - Open Source Project Portal
// Jenkins CI/CD Pipeline
// GitHub -> Jenkins -> Maven -> Docker -> Docker Hub
// =========================================================================

pipeline {

    agent any

    triggers {
        githubPush()
        pollSCM('H/5 * * * *')
    }

    environment {
        DOCKER_REGISTRY       = 'docker.io'
        DOCKER_IMAGE          = 'kishoreavk/open-source-project-portal'
        DOCKER_TAG            = "${BUILD_NUMBER}"
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials-v2'
        MAVEN_WRAPPER         = "${isUnix() ? './mvnw' : '.\\mvnw.cmd'}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }

    stages {

        // ================================================================
        // 1. CHECKOUT
        // ================================================================
        stage('Checkout') {
            steps {
                echo '===> Stage 1: Checking out source code...'

                checkout scm

                echo "Git Commit: ${env.GIT_COMMIT}"
                echo "Git Branch: ${env.GIT_BRANCH}"
                echo "Build Number: ${env.BUILD_NUMBER}"
            }
        }

        // ================================================================
        // 2. MAVEN BUILD
        // ================================================================
        stage('Maven Build') {
            steps {
                echo '===> Stage 2: Compiling Java 21 source code...'

                script {
                    if (isUnix()) {
                        sh 'chmod +x mvnw'
                        sh "${MAVEN_WRAPPER} clean compile -B"
                    } else {
                        bat "${MAVEN_WRAPPER} clean compile -B"
                    }
                }
            }
        }

        // ================================================================
        // 3. MAVEN TEST
        // ================================================================
        stage('Maven Test') {
            steps {
                echo '===> Stage 3: Running tests...'

                script {
                    if (isUnix()) {
                        sh "${MAVEN_WRAPPER} test -B"
                    } else {
                        bat "${MAVEN_WRAPPER} test -B"
                    }
                }
            }

            post {
                always {
                    junit(
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }

        // ================================================================
        // 4. PACKAGE
        // ================================================================
        stage('Package') {
            steps {
                echo '===> Stage 4: Creating Spring Boot JAR...'

                script {
                    if (isUnix()) {
                        sh "${MAVEN_WRAPPER} package -DskipTests -B"
                    } else {
                        bat "${MAVEN_WRAPPER} package -DskipTests -B"
                    }
                }
            }

            post {
                success {
                    archiveArtifacts(
                        artifacts: 'target/*.jar',
                        fingerprint: true
                    )
                }
            }
        }

        // ================================================================
        // 5. DOCKER BUILD
        // ================================================================
        stage('Docker Build') {
            steps {
                echo '===> Stage 5: Building Docker image...'

                script {
                    if (isUnix()) {

                        sh """
                            docker build \
                            -t ${DOCKER_IMAGE}:${DOCKER_TAG} \
                            -t ${DOCKER_IMAGE}:latest .
                        """

                    } else {

                        bat """
                            docker build ^
                            -t ${DOCKER_IMAGE}:${DOCKER_TAG} ^
                            -t ${DOCKER_IMAGE}:latest .
                        """
                    }
                }
            }
        }

        // ================================================================
        // 6. DOCKER LOGIN
        // ================================================================
        stage('Docker Login') {
            steps {
                echo '===> Stage 6: Testing Docker Hub authentication...'

                withCredentials([
                    usernamePassword(
                        credentialsId: env.DOCKER_CREDENTIALS_ID,
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {

                    script {

                        if (isUnix()) {

                            sh '''
                                echo "Jenkins user: $USER"
                                echo "Docker context:"
                                docker context show

                                echo "Docker server version:"
                                docker version --format "{{.Server.Version}}"

                                echo "Docker username from Jenkins credential:"
                                echo "$DOCKER_USER"

                                echo "Attempting Docker Hub login..."

                                printf '%s' "$DOCKER_PASS" | docker login \
                                    --username "$DOCKER_USER" \
                                    --password-stdin
                            '''

                        } else {

                            bat '''
                                echo Jenkins Windows User: %USERNAME%
                                echo Jenkins User Profile: %USERPROFILE%

                                echo Docker Context:
                                docker context show

                                echo Docker Server Version:
                                docker version --format "{{.Server.Version}}"

                                echo Docker Username From Jenkins Credential:
                                echo %DOCKER_USER%

                                echo Attempting Docker Hub login...

                                powershell -NoProfile -Command "$env:DOCKER_PASS | docker login --username $env:DOCKER_USER --password-stdin"

                                if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%
                            '''
                        }
                    }
                }
            }
        }

        // ================================================================
        // 7. DOCKER PUSH
        // ================================================================
        stage('Docker Push') {
            steps {
                echo '===> Stage 7: Pushing Docker image to Docker Hub...'

                script {
                    if (isUnix()) {

                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        sh "docker push ${DOCKER_IMAGE}:latest"

                    } else {

                        bat "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        bat "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }
    }

    // ====================================================================
    // POST ACTIONS
    // ====================================================================
    post {

        success {
            echo '============================================'
            echo 'SUCCESS: CI/CD Pipeline completed!'
            echo "Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
            echo '============================================'
        }

        failure {
            echo '============================================'
            echo 'FAILURE: Pipeline failed.'
            echo "Build Number: ${BUILD_NUMBER}"
            echo '============================================'
        }

        always {
            echo 'Pipeline finished.'
        }
    }
}