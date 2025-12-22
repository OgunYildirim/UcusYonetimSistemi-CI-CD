pipeline {
    agent any
    
    environment {
        DOCKER_BUILDKIT = '1'
        COMPOSE_DOCKER_CLI_BUILD = '1'
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
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

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

        stage('5- Docker Run') {
            steps {
                script {
                    sh '''
                        export DOCKER_BUILDKIT=0
                        # Volume temizliği 400 hatası (duplicate user) almamak için şart
                        docker-compose down -v --remove-orphans || true
                        docker rm -f ucus-yonetim-db ucus-yonetim-backend ucus-yonetim-frontend || true

                        docker-compose build backend
                        docker-compose up -d postgres backend frontend
                    '''

                    echo 'Tabloların oluşması için bekleniyor (60s)...'
                    sleep 60

                    echo 'Veritabanı kontrol ediliyor ve roller yükleniyor...'
                    sh "docker cp backend/src/main/resources/data.sql ucus-yonetim-db:/data.sql"
                    
                    // GARANTİ YÖNTEM: Hem postgres hem flightdb isimli db'leri kontrol edip yükler
                    sh '''
                        if docker exec ucus-yonetim-db psql -U postgres -d flightdb -c "\\dt" | grep -q "roles"; then
                            echo "Tablolar 'flightdb' içinde bulundu. Veriler yükleniyor..."
                            docker exec ucus-yonetim-db psql -U postgres -d flightdb -f /data.sql
                        elif docker exec ucus-yonetim-db psql -U postgres -d postgres -c "\\dt" | grep -q "roles"; then
                            echo "Tablolar 'postgres' içinde bulundu. Veriler yükleniyor..."
                            docker exec ucus-yonetim-db psql -U postgres -d postgres -f /data.sql
                        else
                            echo "HATA: Roller tablosu bulunamadı. Backend loglarına bakın."
                            docker logs ucus-yonetim-backend --tail 100
                            exit 1
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
            echo 'Temizlik yapiliyor...'
            sh 'docker-compose down -v || true'
            cleanWs()
        }
    }
}
