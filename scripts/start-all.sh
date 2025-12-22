#!/bin/bash

# Uçuş Yönetim Sistemi - Complete Startup Guide
# Bu script tüm servisleri sırasıyla başlatır

echo "=================================="
echo "  Uçuş Yönetim Sistemi"
echo "  Local Development Startup"
echo "=================================="
echo ""

PROJECT_ROOT="$(dirname "$0")/.."

# 1. PostgreSQL'i başlat
echo "1️⃣  PostgreSQL Veritabanı Başlatılıyor..."
"$PROJECT_ROOT/scripts/start-db.sh"

if [ $? -ne 0 ]; then
    echo "❌ Veritabanı başlatılamadı!"
    exit 1
fi

echo ""
echo "=================================="
echo ""

# 2. Backend'i başlat
echo "2️⃣  Backend Başlatılıyor..."
echo ""
echo "⚠️  Backend, yeni bir terminal penceresinde başlatılacak."
echo "   Alternatif olarak, manuel başlatmak için:"
echo "   ./scripts/start-backend.sh"
echo ""

# Terminal tip kontrolü
if [ -n "$DISPLAY" ]; then
    # GUI ortamı varsa
    if command -v gnome-terminal &> /dev/null; then
        gnome-terminal --tab --title="Backend" -- bash -c "cd '$PROJECT_ROOT' && ./scripts/start-backend.sh; exec bash"
    elif command -v xterm &> /dev/null; then
        xterm -T "Backend" -e "cd '$PROJECT_ROOT' && ./scripts/start-backend.sh; exec bash" &
    else
        echo "⚠️  GUI terminal bulunamadı. Backend'i manuel başlatın:"
        echo "   ./scripts/start-backend.sh"
    fi
else
    # No GUI, background'da başlat
    echo "📍 Backend arka planda başlatılıyor..."
    (cd "$PROJECT_ROOT" && ./scripts/start-backend.sh > /tmp/backend.log 2>&1 &)
    
    echo "⏳ Backend'in başlaması bekleniyor (30 saniye)..."
    sleep 30
fi

echo ""
echo "=================================="
echo ""

# 3. Frontend'i başlat
echo "3️⃣  Frontend Başlatılıyor..."
echo ""
echo "⚠️  Frontend, yeni bir terminal penceresinde başlatılacak."
echo "   Alternatif olarak, manuel başlatmak için:"
echo "   ./scripts/start-frontend.sh"
echo ""

# Terminal tip kontrolü
if [ -n "$DISPLAY" ]; then
    # GUI ortamı varsa
    if command -v gnome-terminal &> /dev/null; then
        gnome-terminal --tab --title="Frontend" -- bash -c "cd '$PROJECT_ROOT' && ./scripts/start-frontend.sh; exec bash"
    elif command -v xterm &> /dev/null; then
        xterm -T "Frontend" -e "cd '$PROJECT_ROOT' && ./scripts/start-frontend.sh; exec bash" &
    else
        echo "⚠️  GUI terminal bulunamadı. Frontend'i manuel başlatın:"
        echo "   ./scripts/start-frontend.sh"
    fi
else
    # No GUI
    echo "📍 Frontend'i manuel olarak başlatın:"
    echo "   cd $PROJECT_ROOT && ./scripts/start-frontend.sh"
fi

echo ""
echo "=================================="
echo ""
echo "✅ Tüm servisler başlatılıyor!"
echo ""
echo "🌐 Erişim Bilgileri:"
echo "   • Frontend:  http://localhost:3000"
echo "   • Backend:   http://localhost:8080"
echo "   • Swagger:   http://localhost:8080/swagger-ui.html"
echo "   • Database:  localhost:2510"
echo ""
echo "📝 Notlar:"
echo "   • Backend ve Frontend'i yeni terminallerde başlatın"
echo "   • Durdurmak için: ./scripts/stop-all.sh"
echo "   • Log dosyaları: /tmp/backend.log, /tmp/frontend.log"
echo ""
echo "🎉 Başarılar!"
echo "=================================="
