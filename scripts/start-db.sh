#!/bin/bash

# Uçuş Yönetim Sistemi - PostgreSQL Database Starter
# Bu script PostgreSQL veritabanını Docker container olarak başlatır

echo "🗄️  PostgreSQL Veritabanı Başlatılıyor..."

# Container'ın zaten çalışıp çalışmadığını kontrol et
if docker ps -a --format '{{.Names}}' | grep -q "^ucus-yonetim-db$"; then
    echo "ℹ️  'ucus-yonetim-db' container'ı zaten mevcut."
    
    # Container çalışıyor mu kontrol et
    if docker ps --format '{{.Names}}' | grep -q "^ucus-yonetim-db$"; then
        echo "✅ Veritabanı zaten çalışıyor!"
        echo "🌐 PostgreSQL: localhost:2510"
        echo "📊 Database: flight_management_db"
        echo "👤 Username: postgres"
        exit 0
    else
        echo "🔄 Mevcut container başlatılıyor..."
        docker start ucus-yonetim-db
        echo "✅ Veritabanı başlatıldı!"
        echo "🌐 PostgreSQL: localhost:2510"
        exit 0
    fi
fi

# Yeni container oluştur ve başlat
echo "📦 Yeni PostgreSQL container'ı oluşturuluyor..."

docker run --name ucus-yonetim-db \
  -e POSTGRES_DB=flight_management_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=ogen12345 \
  -p 2510:5432 \
  -d postgres:15-alpine

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ PostgreSQL başarıyla başlatıldı!"
    echo ""
    echo "📊 Bağlantı Bilgileri:"
    echo "   Host: localhost"
    echo "   Port: 2510"
    echo "   Database: flight_management_db"
    echo "   Username: postgres"
    echo "   Password: ogen12345"
    echo ""
    echo "⏳ Veritabanının hazır olması için 5 saniye bekleniyor..."
    sleep 5
    echo ""
    echo "✅ Veritabanı hazır! Backend'i başlatabilirsiniz."
else
    echo "❌ Hata: PostgreSQL başlatılamadı!"
    exit 1
fi
