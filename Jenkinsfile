pipeline {
    agent any
    
    environment {
        // Docker BuildKit ayarları
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
        // C DİSKİNİ KURTARAN AYAR: Maven kütüphanelerini D diskine yönlendir
        MAVEN_REPO_PATH = "D:/MavenRepo"
        // Maven komutlarına eklenecek ortak parametre
        MVN_OPTS = "-Dmaven.repo.local=${MAVEN_REPO_PATH}"
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
                    // Maven yerel depoyu D diskinde tutar
                    sh "mvn clean package -DskipTests ${MVN_OPTS}"
                }
            }
        }

        // 3. ASAMA: Birim Testleri (15 Puan)
        stage('3- Unit Tests') {
            steps {
                dir('backend') {
                    sh "mvn test -Dtest=*Test,!*IT,!*IntegrationTest,!SeleniumUserFlowsTest ${MVN_OPTS}"
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
                    sh "mvn test -Dtest=*IT,*IntegrationTest -Dsurefire.failIfNoSpecifiedTests=false ${MVN_OPTS}"
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
                        # 400 HATASI COZUMU: -v ile eski veritabanı kayıtlarını (volume) tamamen sil
                        docker-compose down -v --remove-orphans || true
                        docker rm -f ucus-yonetim-db ucus-yonetim-backend ucus-yonetim-frontend || true
                        
                        # Cache kullanmadan tertemiz build
                        docker-compose build --no-cache backend
                        docker-compose up -d postgres backend frontend
                    '''
                    echo 'Sistemin ve veritabaninin hazir olmasi bekleniyor (60s)...'
                    sleep 60
                    sh 'docker ps'
                }
            }
        }

        // 6. ASAMA: Selenium E2E Senaryolari (55 Puan)
        stage('6-1 Scenario: User Login Flow') {
            steps {
                script {
                    echo 'Running Scenario 1: Login Flow...'
                    // Konteyner içinde de m2 klasörünü sabit tutuyoruz
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
                // HATA ANALİZİ: Başarısız olursa logları bas ki 400 hatasının nedenini görelim
                sh "docker logs ucus-yonetim-backend --tail 100 || true"
                
                // Test raporlarini konteynerden Jenkins'e cekiyoruz
                sh "docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/ || true"
                junit '**/target/surefire-reports/*.xml'
            }
            echo 'Cleaning up resources and pruning docker...'
            sh 'docker-compose down -v || true'
            // Sahipsiz imajları silerek C diskinde yer açar
            sh 'docker system prune -f || true'
            cleanWs()
        }
        success {
            echo 'SUCCESS: All CI/CD stages and 3 Selenium scenarios completed successfully.'
        }
    }
}
