pipeline {
    agent any
    
    environment {
        // BuildKit hatasını ve Docker Compose versiyon farklarını giderir
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
        APP_URL = "http://localhost:8080"
    }

    stages {
        // 1. AŞAMA: Kodların Çekilmesi (5 Puan)
        stage('1- Checkout SCM') {
            steps {
                checkout scm
            }
        }

        // 2. AŞAMA: Build (5 Puan)
        stage('2- Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        // 3. AŞAMA: Birim Testleri (15 Puan)
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

        // 4. AŞAMA: Entegrasyon Testleri (15 Puan)
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

        // 5. AŞAMA: Docker üzerinde çalıştırma (5 Puan)
        stage('5- Docker Run') {
            steps {
                script {
                    // Sistemindeki komuta göre docker-compose veya docker compose otomatik denenir
                    sh 'docker-compose build --pull || docker compose build --pull'
                    sh 'docker-compose down || docker compose down || true'
                    sh 'docker-compose up -d || docker compose up -d'

                    echo 'Sistemin ayağa kalkması bekleniyor (30s)...'
                    sleep 30
                }
            }
        }

        // 6. AŞAMA: E2E Senaryoları (55 Puan)
        // ÖNEMLİ: Hoca en az 3 senaryo istediği için 3 ayrı stage yapıldı.

        stage('6-1 Scenario: User Login Flow') {
            steps {
                script {
                    echo 'Senaryo 1: Login testi başlatılıyor...'
                    sh "docker exec -T ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario1_loginFlows"
                }
            }
        }

        stage('6-2 Scenario: Flight Search') {
            steps {
                script {
                    echo 'Senaryo 2: Uçuş arama testi başlatılıyor...'
                    sh "docker exec -T ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario2_searchFlight"
                }
            }
        }

        stage('6-3 Scenario: Booking Process') {
            steps {
                script {
                    echo 'Senaryo 3: Rezervasyon testi başlatılıyor...'
                    sh "docker exec -T ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario3_booking"
                }
            }
        }
    }

    post {
        always {
            script {
                // Test raporlarını konteynerden Jenkins'e kopyala
                sh "docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/ || true"
                junit '**/target/surefire-reports/*.xml'
            }
            echo 'Temizlik yapılıyor...'
            sh 'docker-compose down || docker compose down || true'
            cleanWs()
        }
        success {
            echo '✅ Tebrikler! Tüm CI/CD aşamaları ve 3 E2E senaryosu başarıyla tamamlandı.'
        }
    }
}