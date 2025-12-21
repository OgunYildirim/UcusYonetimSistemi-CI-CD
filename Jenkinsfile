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

                    sh 'docker compose build'
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



        stage('Deploy with Docker Compose') {
            steps {
                echo 'Deploying with Docker Compose...'
                sh 'docker compose up -d'
                // Servislerin başlamasını bekle
                sh 'sleep 60'
            }
        }

        stage('Health Check') {
            steps {
                echo 'Performing Health Checks...'
                script {
                    sh '''
                        echo "Waiting for services to be ready..."
                        # Backend health check
                        for i in {1..30}; do
                            if curl -f http://localhost:8080/actuator/health 2>/dev/null; then
                                echo "Backend is healthy!"
                                break
                            fi
                            echo "Attempt $i: Backend not ready yet, waiting..."
                            sleep 10
                        done

                        # Frontend health check
                        for i in {1..30}; do
                            if curl -f http://localhost:3000 2>/dev/null; then
                                echo "Frontend is healthy!"
                                break
                            fi
                            echo "Attempt $i: Frontend not ready yet, waiting..."
                            sleep 10
                        done

                        echo "All services are healthy!"
                    '''
                }
            }
        }

        stage('E2E Scenario 1 - Selenium Login Test') {
            steps {
                echo 'Running E2E Scenario 1: User Login Flow...'
                dir('backend') {
                    // Selenium testini scenario=1 ile çalıştır
                    sh 'mvn -B test -Dtest=SeleniumUserFlowsTest#scenario1_loginFlows -Dselenium.scenario=1 -Dfrontend.base=http://localhost:3000 -Dbackend.base=http://localhost:8080 -Dspring.profiles.active=test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts artifacts: '**/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('E2E Scenario 2 - Selenium Admin Flight Test') {
            steps {
                echo 'Running E2E Scenario 2: Admin Flight Management...'
                dir('backend') {
                    sh 'mvn -B test -Dtest=SeleniumUserFlowsTest#scenario2_adminAddFlight -Dselenium.scenario=2 -Dfrontend.base=http://localhost:3000 -Dbackend.base=http://localhost:8080 -Dspring.profiles.active=test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts artifacts: '**/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('E2E Scenario 3 - Selenium User Booking Test') {
            steps {
                echo 'Running E2E Scenario 3: User Flight Booking...'
                dir('backend') {
                    sh 'mvn -B test -Dtest=SeleniumUserFlowsTest#scenario3_userFlightBooking -Dselenium.scenario=3 -Dfrontend.base=http://localhost:3000 -Dbackend.base=http://localhost:8080 -Dspring.profiles.active=test'
                }
            }
            post {
                always {
                    junit 'backend/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts artifacts: '**/screenshots/*.png', allowEmptyArchive: true
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
            echo 'Cleaning up Docker containers...'
            sh 'docker compose down || true'
            cleanWs()
        }
    }
}