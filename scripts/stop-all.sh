#!/bin/bash

# Uçuş Yönetim Sistemi - Stop All Services
# Bu script tüm servisleri durdurur

echo "🛑 Tüm Servisler Durduruluyor..."

# PostgreSQL container'ını durdur
if docker ps --format '{{.Names}}' | grep -q "^ucus-yonetim-db$"; then
    echo "🗄️  PostgreSQL durduruluyor..."
    docker stop ucus-yonetim-db
    echo "✅ PostgreSQL durduruldu"
else
    echo "ℹ️  PostgreSQL zaten durdurulmuş"
fi

# Port 8080'de çalışan process'i bul ve durdur (Backend)
echo ""
echo "🔍 Backend (port 8080) kontrol ediliyor..."
BACKEND_PID=$(lsof -ti:8080 2>/dev/null)
if [ ! -z "$BACKEND_PID" ]; then
    echo "🚀 Backend durduruluyor (PID: $BACKEND_PID)..."
    kill -9 $BACKEND_PID
    echo "✅ Backend durduruldu"
else
    echo "ℹ️  Backend zaten durdurulmuş"
fi

# Port 3000'de çalışan process'i bul ve durdur (Frontend)
echo ""
echo "🔍 Frontend (port 3000) kontrol ediliyor..."
FRONTEND_PID=$(lsof -ti:3000 2>/dev/null)
if [ ! -z "$FRONTEND_PID" ]; then
    echo "🎨 Frontend durduruluyor (PID: $FRONTEND_PID)..."
    kill -9 $FRONTEND_PID
    echo "✅ Frontend durduruldu"
else
    echo "ℹ️  Frontend zaten durdurulmuş"
fi

echo ""
echo "✅ Tüm servisler başarıyla durduruldu!"
