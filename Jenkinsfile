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
                        docker-compose down -v --remove-orphans || true
                        docker rm -f ucus-yonetim-db ucus-yonetim-backend ucus-yonetim-frontend || true

                        docker-compose build backend
                        docker-compose up -d postgres backend frontend
                    '''

                    echo 'Backendin hazir olmasi bekleniyor (60s)...'
                    sleep 60

                    echo 'Veriler yukleniyor...'
                    sh "docker cp backend/src/main/resources/data.sql ucus-yonetim-db:/data.sql"
                    
                    // KONTROLÜ KALDIRDIK - Doğrudan iki veritabanı ismine de deneme yapıyoruz
                    // ON CONFLICT hatalarını görmezden gelmek için || true ekledik
                    sh '''
                        echo "Postgres DB denemesi..."
                        docker exec ucus-yonetim-db psql -U postgres -d postgres -f /data.sql || true
                        
                        echo "Flightdb denemesi (Eger varsa)..."
                        docker exec ucus-yonetim-db psql -U postgres -d flightdb -f /data.sql || true
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
