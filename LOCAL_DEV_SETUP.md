# 🚀 Local Development Setup (Yerel Geliştirme Ortamı Kurulumu)

Bu doküman, Uçuş Yönetim Sistemini yerel geliştirme ortamınızda çalıştırmanız için adım adım talimatlar içerir.

## 📋 Gereksinimler

Aşağıdaki araçların sisteminizde yüklü olması gerekmektedir:

- ✅ Java 17+ (Kontrol: `java -version`)
- ✅ Maven 3.6+ (Kontrol: `mvn -version`)
- ✅ Node.js 16+ (Kontrol: `node -v`)
- ✅ npm (Kontrol: `npm -v`)
- ✅ Docker (Opsiyonel, veritabanı için) (Kontrol: `docker --version`)
- ✅ PostgreSQL 12+ (Docker kullanmıyorsanız)

## 🎯 Hızlı Başlangıç

### Seçenek 1: Kolaylık Scriptleri ile (Önerilen)

```bash
# 1. Veritabanını başlat (Docker ile)
./scripts/start-db.sh

# 2. Backend'i başlat
./scripts/start-backend.sh

# 3. Yeni bir terminalde, Frontend'i başlat
./scripts/start-frontend.sh
```

### Seçenek 2: Manuel Kurulum

#### 1. Veritabanı Kurulumu

**Docker ile (Önerilen):**

```bash
# PostgreSQL container'ını başlat
docker run --name ucus-yonetim-db \
  -e POSTGRES_DB=flight_management_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=ogen12345 \
  -p 2510:5432 \
  -d postgres:15-alpine

# Veritabanının hazır olduğunu kontrol et
docker logs -f ucus-yonetim-db
```

**Manuel PostgreSQL Kurulumu:**

```bash
# PostgreSQL'e bağlan
psql -U postgres

# Veritabanını oluştur
CREATE DATABASE flight_management_db;

# Çıkış
\q
```

#### 2. Backend Kurulumu ve Çalıştırma

```bash
# Backend dizinine git
cd backend

# Bağımlılıkları indir ve projeyi derle
mvn clean install

# Uygulamayı çalıştır
mvn spring-boot:run
```

Backend başarıyla başladığında:
- 🌐 API: `http://localhost:8080`
- 📚 Swagger UI: `http://localhost:8080/swagger-ui.html`
- 📖 API Docs: `http://localhost:8080/api-docs`

#### 3. Frontend Kurulumu ve Çalıştırma

```bash
# Frontend dizinine git
cd frontend

# Bağımlılıkları yükle (ilk seferinde)
npm install

# Development server'ı başlat
npm start
```

Frontend başarıyla başladığında:
- 🌐 Uygulama: `http://localhost:3000`

## 🔧 Yapılandırma

### Backend Yapılandırması

Backend yapılandırması `backend/src/main/resources/application.properties` dosyasındadır:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:2510/flight_management_db
spring.datasource.username=postgres
spring.datasource.password=ogen12345

# JWT Configuration
jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
jwt.expiration=86400000
```

### Frontend Yapılandırması

Frontend API bağlantısı `frontend/src/services/api.js` dosyasında tanımlıdır:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

Farklı bir backend URL'i kullanmak isterseniz, `.env.local` dosyası oluşturabilirsiniz:

```bash
REACT_APP_API_URL=http://localhost:8080/api
```

## 👤 İlk Kullanıcı Oluşturma

### 1. Frontend'den Kayıt Olma

1. `http://localhost:3000` adresini ziyaret edin
2. "Kayıt Ol" butonuna tıklayın
3. Kullanıcı bilgilerinizi girin
4. Sisteme giriş yapın

### 2. Admin Kullanıcısı Oluşturma

Backend ilk çalıştığında otomatik olarak roller oluşturulur. Admin yetkisi vermek için:

```bash
# PostgreSQL'e bağlan
docker exec -it ucus-yonetim-db psql -U postgres -d flight_management_db

# veya yerel PostgreSQL kullanıyorsanız:
psql -U postgres -d flight_management_db

# Kullanıcıya admin rolü ver
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'kullanici_adiniz' AND r.name = 'ROLE_ADMIN';
```

## 📊 Test Verileri

Backend ilk başladığında otomatik olarak şu veriler yüklenir (`data.sql`):

- 2 Rol: `ROLE_USER`, `ROLE_ADMIN`
- 5 Havalimanı: İstanbul (IST), Sabiha Gökçen (SAW), Ankara Esenboğa (ESB), Antalya (AYT), İzmir (ADB)
- 5 Uçak: Boeing 737-800, Airbus A320, Boeing 777-300ER, Airbus A330, ATR 72

## 🎯 Kullanım Senaryoları

### Kullanıcı Senaryosu

1. Ana sayfadan "Kayıt Ol" ile hesap oluşturun
2. Giriş yapın
3. "Uçuşlar" sayfasından uçuş arayın
4. Kalkış havalimanı, varış havalimanı ve tarih seçin
5. Uygun uçuşu bulun ve "Detaylar" butonuna tıklayın
6. Koltuk seçimi yapın (Economy veya Business)
7. Bagaj bilgilerini girin
8. Toplam ücreti görüntüleyin ve rezervasyonu tamamlayın
9. "Biletlerim" sayfasından rezervasyonlarınızı görüntüleyin

### Admin Senaryosu

1. Admin kullanıcısı ile giriş yapın
2. Üst menüden "Admin Panel" seçeneğine tıklayın
3. **Uçuşlar** sekmesinden:
   - Yeni uçuş ekleyin
   - Mevcut uçuşları düzenleyin
   - Uçuş silin
4. **Uçaklar** sekmesinden:
   - Yeni uçak ekleyin
   - Uçak bilgilerini güncelleyin
   - Bakım kaydı oluşturun
5. **Havalimanları** sekmesinden:
   - Yeni havalimanı ekleyin
6. **Fiyatlandırma** sekmesinden:
   - Uçuşlar için fiyat belirleyin
   - Sınıf bazlı fiyatlandırma yapın

## 🔍 API Endpoints

### Authentication
- `POST /api/auth/register` - Yeni kullanıcı kaydı
- `POST /api/auth/login` - Kullanıcı girişi

### Flights (Public)
- `GET /api/flights/all` - Tüm uçuşları listele
- `GET /api/flights/search` - Uçuş ara
- `GET /api/flights/{id}` - Uçuş detayı

### Bookings (Authenticated)
- `POST /api/bookings` - Rezervasyon oluştur
- `GET /api/bookings/my-bookings` - Kullanıcının rezervasyonları
- `PUT /api/bookings/{id}/cancel` - Rezervasyon iptal

### Admin Endpoints
- `/api/admin/airports` - Havalimanı yönetimi
- `/api/admin/aircrafts` - Uçak yönetimi
- `/api/admin/maintenance` - Bakım yönetimi
- `/api/admin/pricing` - Fiyatlandırma yönetimi

Detaylı API dokümantasyonu için: `http://localhost:8080/swagger-ui.html`

## 🛑 Durdurma

### Container'ları Durdurma

```bash
# PostgreSQL container'ını durdur
docker stop ucus-yonetim-db

# Container'ı tamamen kaldır (veriler silinir!)
docker rm ucus-yonetim-db
```

### Backend'i Durdurma

Terminal'de `Ctrl+C` tuşuna basın.

### Frontend'i Durdurma

Terminal'de `Ctrl+C` tuşuna basın.

## 🐛 Sorun Giderme

### Backend Başlamıyor

**Problem:** Port 8080 kullanımda
```bash
# Port'u kullanan process'i bul
lsof -i :8080
# veya
netstat -tulpn | grep 8080

# Process'i durdur
kill -9 <PID>
```

**Problem:** Veritabanına bağlanamıyor
- PostgreSQL'in çalıştığından emin olun: `docker ps | grep postgres`
- Veritabanı bilgilerini kontrol edin: `application.properties`
- Container loglarını kontrol edin: `docker logs ucus-yonetim-db`

### Frontend Başlamıyor

**Problem:** Port 3000 kullanımda
```bash
# Port'u kullanan process'i bul
lsof -i :3000

# Process'i durdur veya frontend'i farklı port'ta çalıştır
PORT=3001 npm start
```

**Problem:** node_modules hataları
```bash
# node_modules ve package-lock.json'u sil
rm -rf node_modules package-lock.json

# Bağımlılıkları yeniden yükle
npm install
```

### CORS Hatası

Backend'in CORS ayarları zaten yapılandırılmış durumda. Eğer hala CORS hatası alıyorsanız:

1. Backend'in `http://localhost:8080` adresinde çalıştığından emin olun
2. Frontend'in `http://localhost:3000` adresinde çalıştığından emin olun
3. Browser console'da hata detaylarını kontrol edin
4. Backend'i yeniden başlatın

### Veritabanı Bağlantı Hatası

```bash
# Docker container'ın çalıştığını kontrol et
docker ps

# Container'ın loglarını kontrol et
docker logs ucus-yonetim-db

# Container içinde PostgreSQL'e bağlan
docker exec -it ucus-yonetim-db psql -U postgres -d flight_management_db

# Tabloları listele
\dt
```

## 📝 Geliştirme Notları

### Hot Reload

- **Backend:** Spring Boot DevTools otomatik yeniden başlatma sağlar
- **Frontend:** React development server otomatik olarak değişiklikleri yansıtır

### Debugging

**Backend:**
```bash
# Debug mode ile başlat
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

**Frontend:**
- Browser'ın Developer Tools'unu kullanın (F12)
- React Developer Tools extension'ını kurun

### Loglama

**Backend:** `application.properties` dosyasında log seviyesini ayarlayın:
```properties
logging.level.com.ucusyonetim=DEBUG
```

**Frontend:** Browser console'da loglar görünür

## 🎨 UI/UX Özellikleri

- 🌙 Dark theme tasarım
- 📱 Responsive (mobil uyumlu)
- ⚡ Hızlı ve akıcı navigasyon
- 🎭 Modern ve kullanıcı dostu arayüz

## 🔒 Güvenlik

- JWT token tabanlı authentication
- BCrypt ile şifrelenmiş parolalar
- Rol bazlı yetkilendirme (RBAC)
- CORS koruması
- XSS ve CSRF koruması

## 📞 Destek

Sorunlarla karşılaşırsanız:
1. Bu dokümandaki "Sorun Giderme" bölümünü kontrol edin
2. Backend loglarını inceleyin
3. Browser console'u kontrol edin
4. Swagger UI'da API'yi test edin

## 🎉 Başarılar!

Artık uygulamayı yerel ortamınızda çalıştırabilir ve backend-frontend entegrasyonunu test edebilirsiniz!
