pipeline {
    agent any
    
    environment {
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
        // C diski dolmasın diye Maven deposunu D'ye yönlendiriyoruz
        MAVEN_OPTS = "-Dmaven.repo.local=D:/MavenRepo"
    }

    stages {
        stage('1- Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('2- Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests -Dmaven.repo.local=D:/MavenRepo'
                }
            }
        }

        stage('3- Unit Tests') {
            steps {
                dir('backend') {
                    sh 'mvn test -Dtest=*Test,!*IT,!*IntegrationTest,!SeleniumUserFlowsTest -Dmaven.repo.local=D:/MavenRepo'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('4- Integration Tests') {
            steps {
                dir('backend') {
                    sh 'mvn test -Dtest=*IT,*IntegrationTest -Dsurefire.failIfNoSpecifiedTests=false -Dmaven.repo.local=D:/MavenRepo'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('5- Docker Run') {
            steps {
                script {
                    sh '''
                        export DOCKER_BUILDKIT=0
                        # Her şeyi ve verileri (Volume) tertemiz yap
                        docker-compose down -v --remove-orphans || true
                        docker rm -f ucus-yonetim-db ucus-yonetim-backend ucus-yonetim-frontend || true

                        # Backend'i inşa et ve sistemi başlat
                        docker-compose build backend
                        docker-compose up -d postgres backend frontend
                    '''

                    echo 'Tabloların oluşması bekleniyor (60s)...'
                    sleep 60

                    echo 'Test verileri ve roller yükleniyor...'
                    sh '''
                        # Dosyayı konteynere kopyala
                        docker cp backend/src/main/resources/data.sql ucus-yonetim-db:/data.sql
                        
                        # Hangi veritabanı aktifse (postgres veya flightdb) ona basmayı dene
                        # Tablo kontrolü yaparak "Relation roles does not exist" hatasını önler
                        if docker exec ucus-yonetim-db psql -U postgres -d postgres -c "\\dt" | grep -q "roles"; then
                            echo "Tablolar postgres veritabanında bulundu. SQL yükleniyor..."
                            docker exec ucus-yonetim-db psql -U postgres -d postgres -f /data.sql
                        elif docker exec ucus-yonetim-db psql -U postgres -d flightdb -c "\\dt" | grep -q "roles"; then
                            echo "Tablolar flightdb veritabanında bulundu. SQL yükleniyor..."
                            docker exec ucus-yonetim-db psql -U postgres -d flightdb -f /data.sql
                        else
                            echo "UYARI: Tablolar henüz oluşmadı veya bulunamadı! Backend logları dökülüyor..."
                            docker logs ucus-yonetim-backend --tail 100
                        fi
                    '''
                    sh 'docker ps'
                }
            }
        }

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
                sh "docker logs ucus-yonetim-backend --tail 50 || true"
                sh "docker cp ucus-yonetim-backend:/app/target/surefire-reports/. backend/target/surefire-reports/ || true"
                junit '**/target/surefire-reports/*.xml'
            }
            echo 'Temizlik yapılıyor...'
            sh 'docker-compose down -v || true'
            // Docker imajlarını temizleyerek yer açar
            sh 'docker system prune -f || true'
            cleanWs()
        }
        success {
            echo 'TEBRİKLER: Tüm CI/CD aşamaları ve Selenium testleri başarıyla tamamlandı!'
        }
    }
}
