#!/bin/bash

# Uçuş Yönetim Sistemi - Backend Starter
# Bu script Spring Boot backend uygulamasını başlatır

echo "🚀 Backend Başlatılıyor..."

# Backend dizinine git
cd "$(dirname "$0")/../backend" || exit 1

echo "📍 Dizin: $(pwd)"

# PostgreSQL'in çalıştığını kontrol et
echo "🔍 PostgreSQL bağlantısı kontrol ediliyor..."
if ! nc -z localhost 2510 2>/dev/null; then
    echo "⚠️  Uyarı: PostgreSQL localhost:2510'da erişilebilir değil!"
    echo "💡 Önce veritabanını başlatın: ./scripts/start-db.sh"
    exit 1
fi

echo "✅ PostgreSQL bağlantısı başarılı!"

# Maven ile projeyi çalıştır
echo ""
echo "📦 Maven ile backend başlatılıyor..."
echo "⏳ Bu işlem birkaç dakika sürebilir..."
echo ""

mvn spring-boot:run

# Script sonlandığında
echo ""
echo "👋 Backend durduruldu."
