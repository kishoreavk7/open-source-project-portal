// =========================================================================
// Jenkins Declarative Pipeline - Open Source Project Portal
// Complete CI/CD: Checkout -> Build -> Test -> Package -> Docker -> Push
// Designed for Automated Webhook & SCM Polling Triggers
// =========================================================================

pipeline {
    agent any

    triggers {
        // Automatically trigger on GitHub Webhook push events
        githubPush()

        // Fallback polling for local demo environments
        // Polls SCM every 5 minutes
        pollSCM('H/5 * * * *')
    }

    environment {
        // Docker Registry Configuration
        DOCKER_REGISTRY       = 'docker.io'
        DOCKER_IMAGE          = 'kishoreavk/open-source-project-portal'
        DOCKER_TAG            = "${BUILD_NUMBER}"

        // Jenkins Docker Hub credential
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials-v2'

        // Dynamic detection for cross-platform agent execution
        MAVEN_WRAPPER         = "${isUnix() ? './mvnw' : '.\\mvnw.cmd'}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }

    stages {

        // =================================================================
        // Stage 1: Checkout Source Code
        // =================================================================
        stage('Checkout') {
            steps {
                echo "===> Stage 1: Checking out source code from Git repository..."

                checkout scm

                script {
                    echo "Current Git Commit: ${env.GIT_COMMIT}"
                    echo "Current Git Branch: ${env.GIT_BRANCH}"
                }
            }
        }

        // =================================================================
        // Stage 2: Maven Compilation
        // =================================================================
        stage('Maven Build') {
            steps {
                echo "===> Stage 2: Compiling Java 21 source code..."

                script {
                    if (isUnix()) {
                        sh "chmod +x mvnw"
                        sh "${MAVEN_WRAPPER} clean compile -B"
                    } else {
                        bat "${MAVEN_WRAPPER} clean compile -B"
                    }
                }
            }
        }

        // =================================================================
        // Stage 3: Maven Automated Testing
        // =================================================================
        stage('Maven Test') {
            steps {
                echo "===> Stage 3: Running Unit and Integration Tests..."

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

        // =================================================================
        // Stage 4: Package Spring Boot JAR
        // =================================================================
        stage('Package') {
            steps {
                echo "===> Stage 4: Packaging Spring Boot Executable JAR artifact..."

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

        // =================================================================
        // Stage 5: Docker Image Build
        // =================================================================
        stage('Docker Build') {
            steps {
                echo "===> Stage 5: Building multi-stage Docker container image..."

                script {
                    if (isUnix()) {
                        sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} -t ${DOCKER_IMAGE}:latest ."
                    } else {
                        bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} -t ${DOCKER_IMAGE}:latest ."
                    }
                }
            }
        }

        // =================================================================
        // Stage 6: Docker Hub Login
        // =================================================================
        stage('Docker Login') {
            steps {
                echo "===> Stage 6: Authenticating with Docker Hub using Jenkins Credentials..."

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
                                echo "$DOCKER_PASS" | docker login \
                                    -u "$DOCKER_USER" \
                                    --password-stdin
                            '''
                        } else {
                            bat '''
                                echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                            '''
                        }
                    }
                }
            }
        }

        // =================================================================
        // Stage 7: Push Docker Image
        // =================================================================
        stage('Docker Push') {
            steps {
                echo "===> Stage 7: Pushing Docker images to Docker Hub..."

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

    // =====================================================================
    // Post-Pipeline Actions
    // =====================================================================
    post {

        success {
            echo "SUCCESS: CI/CD Pipeline completed successfully for build #${BUILD_NUMBER}!"
        }

        failure {
            echo "FAILURE: CI/CD Pipeline encountered an error during build #${BUILD_NUMBER}."
        }

        always {
            echo "Pipeline finished."
        }
    }
}