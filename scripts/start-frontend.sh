#!/bin/bash

# Uçuş Yönetim Sistemi - Frontend Starter
# Bu script React frontend uygulamasını başlatır

echo "🎨 Frontend Başlatılıyor..."

# Frontend dizinine git
cd "$(dirname "$0")/../frontend" || exit 1

echo "📍 Dizin: $(pwd)"

# Backend'in çalıştığını kontrol et
echo "🔍 Backend bağlantısı kontrol ediliyor..."
if ! nc -z localhost 8080 2>/dev/null; then
    echo "⚠️  Uyarı: Backend localhost:8080'de erişilebilir değil!"
    echo "💡 Backend'i başlatmanız önerilir: ./scripts/start-backend.sh"
    echo ""
    read -p "Yine de devam etmek istiyor musunuz? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
else
    echo "✅ Backend bağlantısı başarılı!"
fi

# node_modules yoksa bağımlılıkları yükle
if [ ! -d "node_modules" ]; then
    echo ""
    echo "📦 node_modules bulunamadı. Bağımlılıklar yükleniyor..."
    npm install
    
    if [ $? -ne 0 ]; then
        echo "❌ Hata: Bağımlılıklar yüklenemedi!"
        exit 1
    fi
    echo "✅ Bağımlılıklar başarıyla yüklendi!"
fi

# Development server'ı başlat
echo ""
echo "🚀 React development server başlatılıyor..."
echo "🌐 Uygulama http://localhost:3000 adresinde açılacak"
echo ""

npm start

# Script sonlandığında
echo ""
echo "👋 Frontend durduruldu."
