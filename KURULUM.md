# Uçak Bileti Satış ve Yönetim Sistemi - Kurulum Talimatları

## 📋 Gereksinimler

### Backend
- Java 17 veya üzeri
- Maven 3.6+
- PostgreSQL 12+

### Frontend
- Node.js 16+ ve npm

## 🚀 Kurulum Adımları

### 1. Veritabanı Kurulumu

PostgreSQL'de yeni bir veritabanı oluşturun:

```sql
CREATE DATABASE flight_management_db;
```

### 2. Backend Kurulumu

```bash
cd backend

# application.properties dosyasını düzenleyin
# Veritabanı bağlantı bilgilerinizi güncelleyin:
# spring.datasource.username=postgres
# spring.datasource.password=your_password

# Maven ile projeyi derleyin
mvn clean install

# Uygulamayı çalıştırın
mvn spring-boot:run
```

Backend `http://localhost:8080` adresinde çalışacaktır.

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

### 3. Frontend Kurulumu

```bash
cd frontend

# Bağımlılıkları yükleyin
npm install

# Uygulamayı başlatın
npm start
```

Frontend `http://localhost:3000` adresinde çalışacaktır.

## 👤 İlk Kullanıcı Oluşturma

### Admin Kullanıcısı Oluşturma

Backend çalıştıktan sonra, veritabanında manuel olarak admin kullanıcısı oluşturabilirsiniz:

```sql
-- Önce normal bir kullanıcı kaydedin (frontend'den /register)
-- Sonra o kullanıcıya ADMIN rolü atayın:

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
```

Veya frontend'den kayıt olup yukarıdaki SQL ile admin yapabilirsiniz.

## 📊 Test Verileri

Backend ilk çalıştırıldığında `data.sql` dosyasındaki veriler otomatik yüklenecektir:
- 2 Rol (USER, ADMIN)
- 5 Havalimanı (IST, SAW, ESB, AYT, ADB)
- 5 Uçak

## 🎯 Kullanım

### Kullanıcı İşlemleri
1. Ana sayfadan "Kayıt Ol" butonuna tıklayın
2. Kayıt formunu doldurun
3. Giriş yapın
4. "Uçuşlar" sayfasından uçuş arayın
5. Uçuş seçip rezervasyon yapın
6. "Biletlerim" sayfasından rezervasyonlarınızı görün

### Admin İşlemleri
1. Admin kullanıcısı ile giriş yapın
2. "Admin Panel" menüsüne tıklayın
3. Uçuş, Uçak, Havalimanı ve Fiyatlandırma yönetimi yapın

## 🔑 API Endpoints

### Authentication
- `POST /api/auth/register` - Kayıt ol
- `POST /api/auth/login` - Giriş yap

### Flights (Public)
- `GET /api/flights/all` - Tüm uçuşları listele
- `GET /api/flights/search` - Uçuş ara
- `GET /api/flights/{id}` - Uçuş detayı

### Flights (Admin)
- `POST /api/flights` - Uçuş oluştur
- `PUT /api/flights/{id}` - Uçuş güncelle
- `DELETE /api/flights/{id}` - Uçuş sil

### Bookings (Authenticated)
- `POST /api/bookings` - Rezervasyon oluştur
- `GET /api/bookings/my-bookings` - Rezervasyonlarım
- `PUT /api/bookings/{id}/cancel` - Rezervasyon iptal

### Admin
- `/api/admin/airports` - Havalimanı yönetimi
- `/api/admin/aircrafts` - Uçak yönetimi
- `/api/admin/maintenance` - Bakım yönetimi
- `/api/admin/pricing` - Fiyatlandırma yönetimi

## 🛠️ Teknolojiler

### Backend
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Lombok
- Swagger/OpenAPI

### Frontend
- React 18
- React Router 6
- Axios
- Modern CSS (Dark Theme)

## 📝 Notlar

- JWT token süresi: 24 saat
- Ücretsiz bagaj hakkı: 15kg
- Fazla bagaj ücreti: 50 TL/kg
- Koltuk sınıfları: Economy, Business

## 🐛 Sorun Giderme

### Backend başlamıyor
- PostgreSQL'in çalıştığından emin olun
- Veritabanı bağlantı bilgilerini kontrol edin
- Port 8080'in kullanılmadığından emin olun

### Frontend başlamıyor
- Node.js versiyonunu kontrol edin (16+)
- `npm install` komutunu tekrar çalıştırın
- Port 3000'in kullanılmadığından emin olun

### CORS hatası
- Backend'in `SecurityConfig` dosyasında CORS ayarlarını kontrol edin
- Frontend URL'inin `http://localhost:3000` olduğundan emin olun

## 📧 Destek

Sorularınız için proje sahibi ile iletişime geçin.
