// =========================================================================
// Jenkins Declarative Pipeline - Open Source Project Portal
// Project #91
// CI/CD: Checkout -> Build -> Test -> Package -> Docker Build -> Login -> Push
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
        // STAGE 1 - CHECKOUT
        // ================================================================
        stage('Checkout') {
            steps {
                echo "===> Stage 1: Checking out source code from Git repository..."

                checkout scm

                script {
                    echo "Current Git Commit: ${env.GIT_COMMIT}"
                    echo "Current Git Branch: ${env.GIT_BRANCH}"
                    echo "Build Number: ${env.BUILD_NUMBER}"
                }
            }
        }

        // ================================================================
        // STAGE 2 - MAVEN BUILD
        // ================================================================
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

        // ================================================================
        // STAGE 3 - MAVEN TEST
        // ================================================================
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

        // ================================================================
        // STAGE 4 - PACKAGE
        // ================================================================
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

        // ================================================================
        // STAGE 5 - DOCKER BUILD
        // ================================================================
        stage('Docker Build') {
            steps {
                echo "===> Stage 5: Building multi-stage Docker container image..."

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
        // STAGE 6 - DOCKER LOGIN DIAGNOSTIC
        // ================================================================
        stage('Docker Login') {
            steps {

                echo "===> Stage 6: Testing Docker Hub authentication..."

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
                                echo "Jenkins OS: Unix/Linux"
                                echo "Jenkins User: $USER"

                                echo "Docker Context:"
                                docker context show

                                echo "Docker Server Version:"
                                docker version --format "{{.Server.Version}}"

                                echo "Attempting Docker Hub login..."

                                printf '%s' "$DOCKER_PASS" | docker login \
                                    --username "$DOCKER_USER" \
                                    --password-stdin
                            '''

                        } else {

                            powershell '''
                                Write-Host "============================================"
                                Write-Host "JENKINS DOCKER AUTHENTICATION DIAGNOSTIC"
                                Write-Host "============================================"

                                Write-Host "Jenkins Windows User:"
                                Write-Host $env:USERNAME

                                Write-Host "Jenkins User Profile:"
                                Write-Host $env:USERPROFILE

                                Write-Host "Computer Name:"
                                Write-Host $env:COMPUTERNAME

                                Write-Host ""
                                Write-Host "Docker Context:"
                                docker context show

                                Write-Host ""
                                Write-Host "Docker Context List:"
                                docker context ls

                                Write-Host ""
                                Write-Host "Docker Server Version:"
                                docker version --format "{{.Server.Version}}"

                                Write-Host ""
                                Write-Host "Docker Info:"
                                docker info --format "{{.ServerVersion}}"

                                Write-Host ""
                                Write-Host "Docker Username From Jenkins Credential:"
                                Write-Host $env:DOCKER_USER

                                Write-Host ""
                                Write-Host "Attempting Docker Hub login..."
                                Write-Host "Password is intentionally NOT displayed."

                                $env:DOCKER_PASS | docker login `
                                    --username $env:DOCKER_USER `
                                    --password-stdin

                                if ($LASTEXITCODE -ne 0) {
                                    Write-Host ""
                                    Write-Host "Docker Hub authentication FAILED."
                                    exit $LASTEXITCODE
                                }

                                Write-Host ""
                                Write-Host "Docker Hub authentication SUCCESSFUL."
                            }
                        }
                    }
                }
            }
        }

        // ================================================================
        // STAGE 7 - DOCKER PUSH
        // ================================================================
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

    // ====================================================================
    // POST ACTIONS
    // ====================================================================
    post {

        success {
            echo "============================================"
            echo "SUCCESS!"
            echo "CI/CD Pipeline completed successfully."
            echo "Build Number: ${BUILD_NUMBER}"
            echo "Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
            echo "============================================"
        }

        failure {
            echo "============================================"
            echo "FAILURE!"
            echo "Pipeline encountered an error."
            echo "Build Number: ${BUILD_NUMBER}"
            echo "============================================"
        }

        always {
            echo "Pipeline finished."
        }
    }
}