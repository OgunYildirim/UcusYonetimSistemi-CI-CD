pipeline {
    agent any
    
    environment {
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
        // Maven D: diskini kullansın (C: diski dolmasın)
        MAVEN_OPTS = '-Dmaven.repo.local=D:\\.m2\\repository -Djava.io.tmpdir=D:\\temp'
    }

    tools {
        maven 'Maven'
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
                    bat 'mvn test -Dtest=*Test,!*IT,!*IntegrationTest,!SeleniumUserFlowsTest,!SeleniumBasicFlowsTest'
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

        // 5. ASAMA: Docker Uzerinde Calistirma (5 Puan)
        stage('5- Docker Run') {
            steps {
                script {
                    bat '''
                        set DOCKER_BUILDKIT=0
                        REM 1. Sadece uygulama konteynerlerini temizle (Jenkins'i DOKUNMA!)
                        docker-compose stop backend frontend
                        docker-compose rm -f backend frontend
                        docker rm -f ucus-yonetim-backend ucus-yonetim-frontend

                        REM 2. Konteynerleri insa et ve ayaga kaldir
                        docker-compose build backend
                        docker-compose up -d backend frontend
                    '''

                    echo 'Backend uygulamasinin hazir olmasini bekliyoruz...'
                    echo 'H2 in-memory database kullanildiginda data.sql otomatik yuklenir.'
                    
                    // Backend'in hazir olmasini bekle (max 3 dakika)
                    timeout(time: 3, unit: 'MINUTES') {
                        waitUntil {
                            script {
                                def result = bat(script: 'docker logs ucus-yonetim-backend 2>&1 | findstr "Started FlightManagementApplication"', returnStatus: true)
                                return result == 0
                            }
                        }
                    }

                    echo 'Backend Spring Boot uygulamasi basladi, simdi API endpoint lerinin hazir olmasini bekliyoruz...'
                    
                    // API endpoint'lerinin hazir olmasini bekle
                    timeout(time: 2, unit: 'MINUTES') {
                        waitUntil {
                            script {
                                // Backend container icinden health check
                                def healthCheck = bat(script: 'docker exec ucus-yonetim-backend curl -f http://localhost:8080/actuator/health || exit 1', returnStatus: true)
                                if (healthCheck == 0) {
                                    echo '✅ Health check basarili!'
                                    return true
                                }
                                echo '⏳ Health check basarisiz, tekrar deneniyor...'
                                sleep(5)
                                return false
                            }
                        }
                    }
                    
                    // Ek guvenlik: API'nin tamamen hazir olmasi icin 10 saniye daha bekle
                    echo 'API endpoint lerinin tamamen hazir olmasi icin 10 saniye bekleniyor...'
                    sleep(10)

                    echo '✅ Backend tamamen hazir!'
                    bat 'docker ps'
                }
            }
        }

        // 6. ASAMA: Selenium E2E Senaryolari (55 Puan)
        stage('6-1 Senaryo: Kullanıcı Giriş Akışı') {
            steps {
                script {
                    echo 'Senaryo 1 Çalıştırılıyor: Kullanıcı Giriş Akışı...'
                    // -w /app ile Maven'i konteyner icindeki proje kok dizininde calistiriyoruz
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario1_loginFlows"
                }
            }
        }

        stage('6-2 Senaryo: Uçuş Arama') {
            steps {
                script {
                    echo 'Senaryo 2 Çalıştırılıyor: Uçuş Arama...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario2_searchFlight"
                }
            }
        }

        stage('6-3 Senaryo: Rezervasyon Süreci') {
            steps {
                script {
                    echo 'Senaryo 3 Çalıştırılıyor: Rezervasyon Süreci...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumUserFlowsTest#scenario3_booking"
                }
            }
        }

        // 7 TEMEL E2E TEST SENARYOLARI (Stage 6'dan Farklı)
        stage('7-1 Temel Senaryo: Havaalanı Listeleme') {
            steps {
                script {
                    echo 'Temel Senaryo 1 Çalıştırılıyor: Havaalanı Listeleme...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumBasicFlowsTest#scenario1_listAirports -Dselenium.scenario=1"
                }
            }
        }

        stage('7-2 Temel Senaryo: Tüm Uçuşları Listeleme') {
            steps {
                script {
                    echo 'Temel Senaryo 2 Çalıştırılıyor: Tüm Uçuşları Listeleme...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumBasicFlowsTest#scenario2_listAllFlights -Dselenium.scenario=2"
                }
            }
        }

        stage('7-3 Temel Senaryo: Uçuş Detay Görüntüleme') {
            steps {
                script {
                    echo 'Temel Senaryo 3 Çalıştırılıyor: Uçuş Detay Görüntüleme...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumBasicFlowsTest#scenario3_viewFlightDetails -Dselenium.scenario=3"
                }
            }
        }

        stage('7-4 Temel Senaryo: Uçuş Arama') {
            steps {
                script {
                    echo 'Temel Senaryo 4 Çalıştırılıyor: Uçuş Arama...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumBasicFlowsTest#scenario4_searchFlights -Dselenium.scenario=4"
                }
            }
        }

        stage('7-5 Temel Senaryo: Admin Uçak Ekleme') {
            steps {
                script {
                    echo 'Temel Senaryo 5 Çalıştırılıyor: Admin Uçak Ekleme...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumBasicFlowsTest#scenario5_adminAddAircraft -Dselenium.scenario=5"
                }
            }
        }

        stage('7-6 Temel Senaryo: Admin Havaalanı Ekleme') {
            steps {
                script {
                    echo 'Temel Senaryo 6 Çalıştırılıyor: Admin Havaalanı Ekleme...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumBasicFlowsTest#scenario6_adminAddAirport -Dselenium.scenario=6"
                }
            }
        }

        stage('7-7 Temel Senaryo: Admin Bakım Kaydı Ekleme') {
            steps {
                script {
                    echo 'Temel Senaryo 7 Çalıştırılıyor: Admin Bakım Kaydı Ekleme...'
                    bat "docker exec -w /app ucus-yonetim-backend mvn test -Dtest=SeleniumBasicFlowsTest#scenario7_adminAddMaintenance -Dselenium.scenario=7"
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