pipeline {
    agent any
    
    environment {
        // Docker Compose'un servis isimleri (Selenium testleri için)
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
                    // Sadece Unit testleri çalıştırır (Service ve Controller testleri)
                    sh 'mvn test -Dtest=*Test,!*IT,!*IntegrationTest,!*SeleniumUserFlowsTest'
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
                    // Sadece Integration testleri çalıştırır (*IT ve *IntegrationTest)
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
                    // BuildKit kapalı kalsın (Görüntüdeki hatayı önlemek için)
                    sh 'export DOCKER_BUILDKIT=0 && docker-compose build'
                    sh 'docker-compose down || true'
                    sh 'docker-compose up -d'

                    echo 'Sistemin ayağa kalkması bekleniyor...'
                    sleep 30
                }
            }
        }

        // 6. AŞAMA: E2E Senaryoları (55+ Puan)
        // Her senaryo ayrı bir stage olarak raporlanmalı (Hoca ayrı stage'ler yazılabilir demiş)

        stage('6-1 Scenario: User Login Flow') {
            steps {
                script {
                    echo 'REQ-101 - User Login Flow Test Çalıştırılıyor...'
                    sh "docker exec ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario1_loginFlows -Dselenium.scenario=1"
                }
            }
            post {
                always {
                    sh "docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/ || true"
                    sh "docker cp ucus-yonetim-backend:/app/target/screenshots/. backend/target/screenshots/ || true"
                }
            }
        }

        stage('6-2 Scenario: Admin Add Flight') {
            steps {
                script {
                    echo 'REQ-102 - Admin Add Flight Test Çalıştırılıyor...'
                    sh "docker exec ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario2_adminAddFlight -Dselenium.scenario=2"
                }
            }
            post {
                always {
                    sh "docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/ || true"
                    sh "docker cp ucus-yonetim-backend:/app/target/screenshots/. backend/target/screenshots/ || true"
                }
            }
        }
    }

    post {
        always {
            // Raporları Jenkins arayüzüne ekle
            junit '**/target/surefire-reports/*.xml'

            echo 'Temizlik yapılıyor...'
            sh 'docker-compose down'
            cleanWs()
        }
        success {
            echo 'Tüm aşamalar başarıyla tamamlandı!'
        }
    }
}