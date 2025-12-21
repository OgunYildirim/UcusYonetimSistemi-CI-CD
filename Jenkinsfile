pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'docker.io'
        IMAGE_NAME = 'ucus-yonetim'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }
        
        stage('Build Backend') {
            steps {
                echo 'Building Spring Boot Backend...'
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Unit Tests - Backend') {
            steps {
                echo 'Running Backend Unit Tests...'
                dir('backend') {
                    sh 'mvn clean test'
                }
            }
            post {
                always {
                    script {
                        if (fileExists('backend/target/surefire-reports/TEST-*.xml')) {
                            junit 'backend/target/surefire-reports/TEST-*.xml'
                            publishTestResults testResultsPattern: 'backend/target/surefire-reports/TEST-*.xml'
                        } else {
                            echo 'No test report files found in backend/target/surefire-reports/'
                            sh 'ls -la backend/target/ || echo "Target directory not found"'
                            sh 'find backend -name "*.xml" -type f || echo "No XML files found"'
                        }
                    }
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                echo 'Building React Frontend...'
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                echo 'Running Integration Tests with Testcontainers...'
                dir('backend') {
                    sh 'mvn verify -Dspring.profiles.active=test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                    junit 'backend/target/failsafe-reports/*.xml'
                    publishTestResults testResultsPattern: 'backend/target/surefire-reports/*.xml,backend/target/failsafe-reports/*.xml'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                echo 'Building Docker Images...'
                script {
                    sh 'docker-compose build'
                }
            }
        }
        
        stage('Push Docker Images') {
            when {
                branch 'main'
            }
            steps {
                echo 'Pushing Docker Images to Registry...'
                script {
                    docker.withRegistry('', DOCKER_CREDENTIALS_ID) {
                        sh 'docker-compose push'
                    }
                }
            }
        }
        
        stage('Deploy to Test Environment') {
            steps {
                echo 'Deploying to Test Environment...'
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }
        
        stage('E2E Tests - Selenium') {
            steps {
                echo 'Running Selenium E2E Tests...'
                dir('backend') {
                    sh 'mvn test -Dtest=SeleniumTestRunner -Dspring.profiles.active=test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/*.xml'
                    publishTestResults testResultsPattern: 'backend/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: '**/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo 'Performing Health Checks...'
                script {
                    sh '''
                        echo "Waiting for services to be ready..."
                        sleep 30
                        
                        echo "Checking Backend Health..."
                        curl -f http://localhost:8080/actuator/health || exit 1
                        
                        echo "Checking Frontend..."
                        curl -f http://localhost:3000 || exit 1
                        
                        echo "All services are healthy!"
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo '✅ Pipeline completed successfully!'
            emailext (
                subject: "✅ Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build completed successfully. Check console output at ${env.BUILD_URL}",
                to: 'team@example.com'
            )
        }
        failure {
            echo '❌ Pipeline failed!'
            emailext (
                subject: "❌ Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build failed. Check console output at ${env.BUILD_URL}",
                to: 'team@example.com'
            )
        }
        always {
            echo 'Cleaning up...'
            cleanWs()
        }
    }
}
