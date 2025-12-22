# Scripts Dizini

Bu dizin, Uçuş Yönetim Sistemini yerel ortamda kolayca çalıştırmak için yardımcı scriptler içerir.

## 📜 Mevcut Scriptler

### `start-all.sh` - Tüm Servisleri Başlat
Tüm servisleri (PostgreSQL, Backend, Frontend) sırasıyla başlatır.

```bash
./scripts/start-all.sh
```

### `start-db.sh` - Veritabanını Başlat
PostgreSQL veritabanını Docker container olarak başlatır.

```bash
./scripts/start-db.sh
```

**Veritabanı Bilgileri:**
- Host: localhost
- Port: 2510
- Database: flight_management_db
- Username: postgres
- Password: ogen12345

### `start-backend.sh` - Backend'i Başlat
Spring Boot backend uygulamasını başlatır.

```bash
./scripts/start-backend.sh
```

**Backend Erişim:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### `start-frontend.sh` - Frontend'i Başlat
React frontend uygulamasını başlatır.

```bash
./scripts/start-frontend.sh
```

**Frontend Erişim:**
- Uygulama: http://localhost:3000

### `stop-all.sh` - Tüm Servisleri Durdur
Çalışan tüm servisleri (PostgreSQL, Backend, Frontend) durdurur.

```bash
./scripts/stop-all.sh
```

## 🚀 Kullanım

### İlk Kurulum

1. **Tüm servisleri başlat:**
   ```bash
   ./scripts/start-all.sh
   ```

2. **Veya adım adım başlat:**
   ```bash
   # 1. Veritabanı
   ./scripts/start-db.sh
   
   # 2. Backend (yeni terminal)
   ./scripts/start-backend.sh
   
   # 3. Frontend (yeni terminal)
   ./scripts/start-frontend.sh
   ```

### Servisleri Durdurma

```bash
./scripts/stop-all.sh
```

## 📝 Notlar

- Scriptler çalıştırılabilir (`chmod +x`) olarak ayarlanmıştır
- Her script kendi içinde hata kontrolü yapar
- Loglar terminal'de gösterilir
- Backend ve Frontend için ayrı terminal penceresi önerilir

## 🔍 Sorun Giderme

Scriptleri çalıştırırken sorun yaşıyorsanız:

1. Scriptlerin çalıştırılabilir olduğundan emin olun:
   ```bash
   chmod +x scripts/*.sh
   ```

2. Docker'ın çalıştığından emin olun:
   ```bash
   docker ps
   ```

3. Gerekli portların kullanılabilir olduğundan emin olun:
   ```bash
   lsof -i :2510  # PostgreSQL
   lsof -i :8080  # Backend
   lsof -i :3000  # Frontend
   ```

## 📚 Daha Fazla Bilgi

Detaylı kurulum talimatları için `LOCAL_DEV_SETUP.md` dosyasına bakın.
