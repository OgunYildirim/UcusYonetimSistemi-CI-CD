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
                    // Sadece Unit testleri çalıştır, E2E ve Integration testlerini exclude et
                    sh 'mvn -B test -Dtest=!*Integration*,!SeleniumUserFlowsTest'
                }
            }
            post {
                always {
                    script {
                        if (fileExists('backend/target/surefire-reports/TEST-*.xml')) {
                            junit 'backend/target/surefire-reports/TEST-*.xml'
                        }
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo 'Running Integration Tests...'
                dir('backend') {
                    // Sadece IT ile bitenleri çalıştırır
                    sh 'mvn -B verify -DskipUnitTests -Dit.test=*IT,*IntegrationTest -Dtest=!SeleniumUserFlowsTest'
                }
            }
            post {
                always {
                    script {
                        if (fileExists('backend/target/failsafe-reports/*.xml')) {
                            junit 'backend/target/failsafe-reports/*.xml'
                        }
                    }
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                echo 'Building Docker Images without Buildx...'
                script {
                    // DOCKER_BUILDKIT=0 değişkeni Buildx gereksinimini ortadan kaldırır
                    sh 'export DOCKER_BUILDKIT=0 && docker-compose build'
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                echo 'Deploying with Docker Compose...'
                // Önce temizle sonra ayağa kaldır
                sh 'docker compose down || docker-compose down || true'
                sh 'docker compose up -d || docker-compose up -d'
                echo 'Waiting for services to be ready (60s)...'
                sleep 60
            }
        }

        stage('Health Check') {
            steps {
                echo 'Performing Health Checks...'
                script {
                    // Basit curl check
                    sh 'curl -f http://localhost:3000 || (echo "Frontend not ready" && exit 1)'
                    sh 'curl -f http://localhost:8080/actuator/health || echo "Backend health check skipped"'
                }
            }
        }

        stage('E2E Tests - Scenario 1') {
            steps {
                echo 'Running E2E Scenario 1: User Login...'
                script {
                    // Karmaşık java komutu yerine konteyner içinde Maven kullanmak daha garantidir
                    sh 'docker exec ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest -Dselenium.scenario=1 -Dfrontend.base=http://ucus-yonetim-frontend -Dbackend.base=http://localhost:8080'
                }
            }
            post {
                always {
                    // Test sonuçlarını ve screenshotları Jenkins'e çek
                    sh 'docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/ || true'
                    sh 'docker cp ucus-yonetim-backend:/app/target/screenshots/. backend/target/screenshots/ || true'
                    junit 'backend/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts artifacts: 'backend/target/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed! Check logs and screenshots.'
        }
        always {
            echo 'Cleaning up...'
            // Test sonrası sistemi kapatmak istemiyorsanız burayı yorum satırı yapabilirsiniz
            sh 'docker compose down || docker-compose down || true'
            cleanWs()
        }
    }
}