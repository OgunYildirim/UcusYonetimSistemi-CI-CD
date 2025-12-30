# ✈️ Uçak Bileti Satış ve Yönetim Sistemi

Tam kapsamlı Spring Boot + React ile geliştirilmiş uçak bileti satış ve yönetim platformu.

## 🚀 Teknolojiler

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA (Hibernate)**
- **Spring Security + JWT**
- **PostgreSQL**
- **Lombok**
- **Swagger/OpenAPI**
- **Maven**

### Frontend
- **React 18**
- **React Router**
- **Axios**
- **CSS3**
- **Responsive Design**

## 📊 Veritabanı Tabloları

1. **users** - Kullanıcı bilgileri
2. **roles** - Rol tanımları (ADMIN, USER)
3. **flights** - Uçuş bilgileri
4. **aircrafts** - Uçak bilgileri
5. **airports** - Havalimanı bilgileri
6. **tickets** - Bilet kayıtları
7. **payments** - Ödeme bilgileri
8. **baggage** - Bagaj bilgileri
9. **seats** - Koltuk bilgileri
10. **aircraft_maintenance** - Uçak bakım kayıtları
11. **flight_pricing** - Uçuş fiyatlandırma
12. **booking** - Rezervasyon bilgileri

## 👥 Roller ve Yetkiler

### USER (Kullanıcı)
- ✅ Kayıt ol / Giriş yap
- ✅ Uçuşları listeleme
- ✅ Uçuş arama (kalkış – varış – tarih)
- ✅ Bilet satın alma
- ✅ Koltuk seçimi
- ✅ Bagaj ekleme
- ✅ Toplam ücret görüntüleme
- ✅ Satın aldığı biletleri listeleme
- ✅ Bilet iptali

### ADMIN
- ✅ Uçak ekleme / silme / güncelleme
- ✅ Uçuş ekleme / silme / güncelleme
- ✅ Havalimanı ekleme
- ✅ Uçak bakımı ekleme
- ✅ Bagaj ücretlerini yönetme
- ✅ Uçuş fiyatlandırması
- ✅ Kullanıcıları görüntüleme
- ✅ Raporlama (satılan biletler)

## 🏗️ Proje Yapısı

```
UcusYonetimTest/
├── backend/                 # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/ucusyonetim/
│   │   │   │       ├── controller/
│   │   │   │       ├── service/
│   │   │   │       ├── repository/
│   │   │   │       ├── entity/
│   │   │   │       ├── dto/
│   │   │   │       ├── config/
│   │   │   │       └── exception/
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
└── frontend/               # React Frontend
    ├── public/
    ├── src/
    │   ├── components/
    │   ├── pages/
    │   ├── services/
    │   └── App.js
    └── package.json
```

## 🔧 Kurulum

### Backend Kurulumu

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend varsayılan olarak `http://localhost:8080` adresinde çalışacaktır.

### Frontend Kurulumu

```bash
cd frontend
npm install
npm start
```

Frontend varsayılan olarak `http://localhost:3000` adresinde çalışacaktır.

## 📝 API Dokümantasyonu

Swagger UI: `http://localhost:8080/swagger-ui.html`

## 💰 Ücretlendirme Sistemi

- **Baz Fiyat**: Her uçuşun temel ücreti
- **Bagaj**: 15kg ücretsiz, fazlası kg başına ücretli
- **Koltuk Tipi**:
  - Economy (standart)
  - Business (ek ücretli)
- **Toplam Ücret**: Otomatik hesaplama

## 🔐 Güvenlik

- JWT tabanlı kimlik doğrulama
- Rol bazlı yetkilendirme (RBAC)
- Password encryption (BCrypt)
- CORS yapılandırması


