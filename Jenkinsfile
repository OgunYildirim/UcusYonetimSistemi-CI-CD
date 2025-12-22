pipeline {
    agent any
    
    environment {
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
        APP_URL = "http://localhost:8080"
    }

    stages {
        // 1. ASAMA: Kodlarin Cekilmesi (5 Puan)
        stage('1- Checkout SCM') {
            steps {
                checkout scm
            }
        }

        // 2. ASAMA: Build (5 Puan)
        stage('2- Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        // 3. ASAMA: Birim Testleri (15 Puan)
        stage('3- Unit Tests') {
            steps {
                dir('backend') {
                    sh 'mvn test -Dtest=*Test,!*IT,!*IntegrationTest,!SeleniumUserFlowsTest'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        // 4. ASAMA: Entegrasyon Testleri (15 Puan)
        stage('4- Integration Tests') {
            steps {
                dir('backend') {
                    sh 'mvn test -Dtest=*IT,*IntegrationTest -Dsurefire.failIfNoSpecifiedTests=false'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        // 5. ASAMA: Docker Uzerinde Calistirma (5 Puan)
        stage('5- Docker Run') {
            steps {
                script {
                    sh '''
                        export DOCKER_BUILDKIT=0
                        # -v bayragi veritabani verilerini tamamen temizler (400 hatasi cozumu)
                        docker-compose down -v --remove-orphans || true
                        docker rm -f ucus-yonetim-db ucus-yonetim-backend ucus-yonetim-frontend || true
                        docker-compose build postgres backend frontend
                        docker-compose up -d postgres backend frontend
                    '''
                    echo 'Waiting for services to be ready (45s)...'
                    sleep 45
                    sh 'docker ps'
                }
            }
        }

        // 6. ASAMA: E2E Senaryolari (55 Puan)
        stage('6-1 Scenario: User Login Flow') {
            steps {
                script {
                    echo 'Running Scenario 1: Login Flow...'
                    sh "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario1_loginFlows"
                }
            }
        }

        stage('6-2 Scenario: Flight Search') {
            steps {
                script {
                    echo 'Running Scenario 2: Flight Search...'
                    sh "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario2_searchFlight"
                }
            }
        }

        stage('6-3 Scenario: Booking Process') {
            steps {
                script {
                    echo 'Running Scenario 3: Booking Process...'
                    sh "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario3_booking"
                }
            }
        }
    }

    post {
        always {
            script {
                // Hata durumunda loglari gorerek analizi kolaylastiriyoruz
                sh "docker logs ucus-yonetim-backend --tail 50 || true"

                // Test raporlarini konteynerden Jenkins'e cekiyoruz
                sh "docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/ || true"
                junit '**/target/surefire-reports/*.xml'
            }
            echo 'Cleaning up resources...'
            // Temizlik asamasinda da hacimleri temizlemek yer kazanmanizi saglar
            sh 'docker-compose down -v || true'
            cleanWs()
        }
        success {
            echo 'SUCCESS: All CI/CD stages and 3 Selenium scenarios completed successfully.'
        }
    }
}