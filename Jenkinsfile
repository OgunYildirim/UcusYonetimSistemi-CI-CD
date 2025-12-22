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

        // 5. ASAMA: Docker Uzerinde Calistirma ve Veritabani Hazirligi (5 Puan)
        stage('5- Docker Run') {
            steps {
                script {
                    sh '''
                        export DOCKER_BUILDKIT=0
                        # 1. Eski konteyner ve verileri (Volume) temizle
                        docker-compose down -v --remove-orphans || true
                        docker rm -f ucus-yonetim-db ucus-yonetim-backend ucus-yonetim-frontend || true

                        # 2. Konteynerleri insa et ve ayaga kaldir
                        docker-compose build backend
                        docker-compose up -d postgres backend frontend
                    '''

                    echo 'Veritabani tablolarinin olusmasi bekleniyor (45s)...'
                    sleep 45

                    // 3. ROLLERIN YUKLENMESI (Role is not found hatasi cozumu)
                    // Paylastigin SQL dosyasinin backend/src/main/resources/data.sql yolunda oldugunu varsayiyoruz.
                    // Veritabani adinin 'flightdb' oldugunu varsayiyoruz (application.properties'den kontrol et).
                    echo 'Test verileri ve roller yukleniyor...'
                    sh "docker cp backend/src/main/resources/data.sql ucus-yonetim-db:/data.sql"
                    // Jenkinsfile 5. Stage içindeki psql satırını şununla değiştir:
                    sh "docker exec ucus-yonetim-db psql -U postgres -d postgres -f /data.sql"

                    echo 'Sistem tamamen hazir.'
                    sh 'docker ps'
                }
            }
        }

        // 6. ASAMA: Selenium E2E Senaryolari (55 Puan)
        stage('6-1 Scenario: User Login Flow') {
            steps {
                script {
                    echo 'Running Scenario 1: Login Flow...'
                    // -w /app ile Maven'i konteyner icindeki proje kok dizininde calistiriyoruz
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
                // Hata analizi icin loglari Jenkins konsoluna bas
                sh "docker logs ucus-yonetim-backend --tail 50 || true"

                // Test raporlarini konteynerden alip Jenkins arayuzunde goster
                sh "docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/ || true"
                junit '**/target/surefire-reports/*.xml'
            }
            echo 'Temizlik yapiliyor...'
            sh 'docker-compose down -v || true'
            cleanWs()
        }
        success {
            echo 'TEBRIKLER: Tüm CI/CD aşamaları ve Selenium testleri başarıyla tamamlandı!'
        }
    }
}
