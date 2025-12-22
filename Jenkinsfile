stage('5- Docker Run') {
            steps {
                script {
                    sh '''
                        docker-compose down -v --remove-orphans || true
                        docker-compose up -d postgres backend frontend
                    '''
                    
                    echo 'Backend loglarindan tablolari kontrol ediyoruz...'
                    sleep 60
                    
                    sh "docker cp backend/src/main/resources/data.sql ucus-yonetim-db:/data.sql"
                    
                    // Tablolarin nerede oldugunu bulmak icin tum semalari tarayarak SQL basan komut
                    sh '''
                        echo "Mevcut tablolar listeleniyor (Analiz icin):"
                        docker exec ucus-yonetim-db psql -U postgres -d postgres -c "\\dt *.*"
                        
                        echo "Veriler yukleniyor..."
                        docker exec ucus-yonetim-db psql -U postgres -d postgres -f /data.sql || \
                        docker exec ucus-yonetim-db psql -U postgres -d postgres -c "SET search_path TO public; \\i /data.sql"
                    '''
                }
            }
        }
