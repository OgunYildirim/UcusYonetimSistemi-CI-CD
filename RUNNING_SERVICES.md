# 🎉 Çalışan Servisler / Running Services

Bu doküman, şu anda yerel ortamda çalışan servisleri gösterir.

## ✅ Aktif Servisler

### 🗄️ PostgreSQL Database
- **Status:** ✅ Running
- **Container:** ucus-yonetim-db
- **Host:** localhost
- **Port:** 2510
- **Database:** flight_management_db
- **Username:** postgres
- **Password:** ogen12345

**Bağlantı:**
```bash
docker exec -it ucus-yonetim-db psql -U postgres -d flight_management_db
```

### 🚀 Backend (Spring Boot)
- **Status:** ✅ Running
- **URL:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs
- **Port:** 8080

**Test:**
```bash
curl http://localhost:8080/api/airports
```

### 🎨 Frontend (React)
- **Status:** ✅ Running
- **URL:** http://localhost:3000
- **Port:** 3000

**Features:**
- Ana Sayfa (Home)
- Uçuş Arama (Flight Search)
- Kullanıcı Kaydı (Registration)
- Giriş (Login)

## 📊 Yüklenen Test Verileri

### Roller (Roles)
- ROLE_USER
- ROLE_ADMIN
- ROLE_STAFF

### Kullanıcılar (Users)
- **admin** / admin123 (Admin yetkili)
- **john.doe** / password123
- **jane.smith** / password123

### Havalimanları (Airports)
- IST - Istanbul Airport
- SAW - Sabiha Gokcen Airport
- ESB - Esenboga Airport

## 🔗 Hızlı Erişim Linkleri

| Servis | URL | Açıklama |
|--------|-----|----------|
| Frontend | http://localhost:3000 | Ana uygulama |
| Backend API | http://localhost:8080/api | REST API |
| Swagger UI | http://localhost:8080/swagger-ui.html | API Dokümantasyonu |
| PostgreSQL | localhost:2510 | Veritabanı |

## 🛠️ Yönetim Komutları

### Servisleri Durdurma
```bash
# Tüm servisleri durdur
./scripts/stop-all.sh

# Sadece database
docker stop ucus-yonetim-db

# Backend ve Frontend için terminal'de Ctrl+C
```

### Logları Görüntüleme
```bash
# Database logs
docker logs -f ucus-yonetim-db

# Backend logs - terminal'de görünür
# Frontend logs - terminal'de görünür
```

### Veritabanı İşlemleri
```bash
# Tabloları listele
docker exec -it ucus-yonetim-db psql -U postgres -d flight_management_db -c "\dt"

# Havalimanlarını göster
docker exec -it ucus-yonetim-db psql -U postgres -d flight_management_db -c "SELECT * FROM airports;"

# Kullanıcıları göster
docker exec -it ucus-yonetim-db psql -U postgres -d flight_management_db -c "SELECT username, email FROM users;"
```

## 🎯 Sonraki Adımlar

1. **Kullanıcı Kaydı:** http://localhost:3000/register adresinden yeni kullanıcı oluşturun
2. **Giriş Yapın:** Kayıt olduğunuz kullanıcı ile giriş yapın
3. **Uçuş Ara:** Havalimanlarını seçerek uçuş arayın
4. **Admin Panel:** Admin kullanıcısı ile giriş yaparak yönetim paneline erişin

## 📝 Notlar

- Backend ve frontend başarıyla entegre edildi
- Tüm API endpoint'leri çalışıyor
- Swagger UI üzerinden API'leri test edebilirsiniz
- Frontend, backend'den havalimanı verilerini çekiyor
- CORS yapılandırması aktif ve çalışıyor

## 🐛 Sorun Giderme

Eğer bir servis düzgün çalışmıyorsa:

1. Servislerin çalıştığını kontrol edin:
```bash
docker ps | grep ucus-yonetim-db
lsof -i :8080  # Backend
lsof -i :3000  # Frontend
```

2. Logları kontrol edin
3. `LOCAL_DEV_SETUP.md` dosyasındaki "Sorun Giderme" bölümüne bakın

---

**Oluşturulma Tarihi:** 2025-12-22
**Status:** ✅ Tüm servisler aktif ve çalışıyor
