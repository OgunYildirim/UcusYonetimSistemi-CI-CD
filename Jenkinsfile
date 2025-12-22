pipeline {
    agent any
    
    environment {
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
    }

    stages {
        // 1. ASAMA: Kodlarin GitHub'dan Cekilmesi (5 Puan)
        stage('1- Checkout SCM') {
            steps {
                checkout scm
            }
        }

        // 2. ASAMA: Maven ile Derleme (5 Puan)
        stage('2- Build Backend') {
            steps {
                dir('backend') {
                    // C diskinin dolmamasi icin D diski MavenRepo kullanimi tavsiye edilir
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        // 3. ASAMA: Birim Testleri (15 Puan)
        stage('3- Unit Tests') {
            steps {
                dir('backend') {
                    bat 'mvn test -Dtest=*Test,!*IT,!*IntegrationTest,!SeleniumUserFlowsTest'
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
                    bat 'mvn test -Dtest=*IT,*IntegrationTest -Dsurefire.failIfNoSpecifiedTests=false'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        // 5. ASAMA: Docker Uzerinde Calistirma ve Veritabani Hazirligi (5 Puan)
        stage('5- Docker Run') {
            steps {
                script {
                    bat '''
                        set DOCKER_BUILDKIT=0
                        REM 1. Sadece uygulama konteynerlerini temizle (Jenkins'i DOKUNMA!)
                        docker-compose stop postgres backend frontend
                        docker-compose rm -f postgres backend frontend
                        docker volume rm ucusyonetimtest_postgres_data
                        docker rm -f ucus-yonetim-db ucus-yonetim-backend ucus-yonetim-frontend

                        REM 2. Konteynerleri insa et ve ayaga kaldir
                        docker-compose build backend
                        docker-compose up -d postgres backend frontend
                    '''

                    echo 'Backend uygulamasinin baslangic verilerini yuklemesi bekleniyor (60s)...'
                    echo 'Spring Boot application.properties ayarinda spring.sql.init.mode=always oldugu icin data.sql otomatik yuklenir.'
                    sleep 60

                    echo '========================================='
                    echo 'VERITABANI KONTROL: Rollerin yuklendigini dogruluyoruz...'
                    echo '========================================='
                    bat 'docker exec ucus-yonetim-db psql -U postgres -d ucusyonetim -c "SELECT * FROM roles;"'
                    
                    echo '========================================='
                    echo 'VERITABANI KONTROL: Kullanicilarin yuklendigini dogruluyoruz...'
                    echo '========================================='
                    bat 'docker exec ucus-yonetim-db psql -U postgres -d ucusyonetim -c "SELECT id, username, email FROM users;"'

                    echo 'Sistem tamamen hazir.'
                    bat 'docker ps'
                }
            }
        }

        // 6. ASAMA: Selenium E2E Senaryolari (55 Puan)
        stage('6-1 Scenario: User Login Flow') {
            steps {
                script {
                    echo 'Running Scenario 1: Login Flow...'
                    // -w /app ile Maven'i konteyner icindeki proje kok dizininde calistiriyoruz
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario1_loginFlows"
                }
            }
        }

        stage('6-2 Scenario: Flight Search') {
            steps {
                script {
                    echo 'Running Scenario 2: Flight Search...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario2_searchFlight"
                }
            }
        }

        stage('6-3 Scenario: Booking Process') {
            steps {
                script {
                    echo 'Running Scenario 3: Booking Process...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario3_booking"
                }
            }
        }
    }

    post {
        always {
            script {
                // Hata analizi icin loglari Jenkins konsoluna bas
                bat "docker logs ucus-yonetim-backend --tail 50"

                // Test raporlarini konteynerden alip Jenkins arayuzunde goster
                bat "docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/"
                junit '**/target/surefire-reports/*.xml'
            }
            echo 'Temizlik yapiliyor...'
            bat 'docker-compose down -v'
            cleanWs()
        }
        success {
            echo 'TEBRIKLER: Tüm CI/CD aşamaları ve Selenium testleri başarıyla tamamlandı!'
        }
    }
}