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
                    // Sadece Unit testleri çalıştır, isminde "Integration" geçenleri atla
                    sh 'mvn -B test -Dtest=!*Integration*'
                }
            }
            post {
                always {
                    script {
                        if (fileExists('backend/target/surefire-reports/TEST-*.xml')) {
                            junit 'backend/target/surefire-reports/TEST-*.xml'
                        } else {
                            echo 'No unit test reports found.'
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo 'Running Integration Tests with Failsafe...'
                dir('backend') {
                    // Sadece entegrasyon testlerini (IT) çalıştırır
                    sh 'mvn -B verify -DskipUnitTests'
                }
            }
            post {
                always {
                    // Hem surefire hem failsafe raporlarını topla
                    junit 'backend/target/*-reports/*.xml'
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
                        curl -f http://localhost:8080/actuator/health || exit 1
                        echo "All services are healthy!"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed!'
        }
        always {
            echo 'Cleaning up...'
            cleanWs()
        }
    }
}