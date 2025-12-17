# 🚀 Uçuş Yönetim Sistemi - CI/CD Kılavuzu

## 📋 Gereksinimler

- Docker Desktop
- Docker Compose
- Jenkins (opsiyonel, CI/CD için)
- Git

---

## 🐳 Docker ile Çalıştırma

### 1. Tüm Servisleri Başlat

```bash
docker-compose up -d
```

Bu komut şunları başlatır:
- PostgreSQL (port 5432)
- Spring Boot Backend (port 8080)
- React Frontend (port 3000)

### 2. Logları İzle

```bash
docker-compose logs -f
```

### 3. Servisleri Durdur

```bash
docker-compose down
```

### 4. Veritabanı ile Birlikte Temizle

```bash
docker-compose down -v
```

---

## 🔧 Manuel Build

### Backend Build

```bash
cd backend
mvn clean package
java -jar target/*.jar
```

### Frontend Build

```bash
cd frontend
npm install
npm run build
npm start
```

---

## 🧪 Test Çalıştırma

### Unit Tests

```bash
cd backend
mvn test
```

### Integration Tests (Testcontainers)

```bash
cd backend
mvn verify
```

### E2E Tests (Selenium)

```bash
cd backend
mvn test -Dtest=SeleniumTestRunner
```

---

## 🏗️ Jenkins Pipeline

### Jenkins Kurulumu

1. Jenkins'i başlat
2. "New Item" → "Pipeline" seç
3. Pipeline definition: "Pipeline script from SCM"
4. SCM: Git
5. Repository URL: `<your-git-repo>`
6. Script Path: `Jenkinsfile`

### Pipeline Aşamaları

1. **Checkout** - Kodu çek
2. **Build Backend** - Maven build
3. **Unit Tests** - JUnit testleri
4. **Build Frontend** - npm build
5. **Integration Tests** - Testcontainers
6. **Build Docker Images** - Docker build
7. **Push Images** - Docker registry'ye push
8. **Deploy** - Test ortamına deploy
9. **E2E Tests** - Selenium testleri
10. **Health Check** - Servis sağlık kontrolü

---

## 📊 Erişim Bilgileri

### Uygulamalar

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

### Veritabanı

- **Host**: localhost
- **Port**: 5432
- **Database**: ucusyonetim
- **Username**: postgres
- **Password**: postgres

### Default Kullanıcılar

**Admin:**
- Username: `ogun`
- Password: `admin`

**User:**
- Username: `user`
- Password: `password`

---

## 🔍 Sorun Giderme

### Docker Container Logları

```bash
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres
```

### Container'ları Yeniden Başlat

```bash
docker-compose restart backend
docker-compose restart frontend
```

### Veritabanını Sıfırla

```bash
docker-compose down -v
docker-compose up -d
```

### Port Çakışması

Eğer portlar kullanımdaysa, `docker-compose.yml` dosyasında portları değiştirin:

```yaml
ports:
  - "8081:8080"  # Backend
  - "3001:80"    # Frontend
```

---

## 📝 Geliştirme Notları

### Hot Reload (Development)

Frontend için:
```bash
cd frontend
npm start
```

Backend için:
```bash
cd backend
mvn spring-boot:run
```

### Database Migration

Hibernate otomatik migration kullanıyor:
```properties
spring.jpa.hibernate.ddl-auto=update
```

Production için:
```properties
spring.jpa.hibernate.ddl-auto=validate
```

---

## 🎯 Proje Yapısı

```
UcusYonetimTest/
├── backend/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── docker-compose.yml
├── Jenkinsfile
└── README-CICD.md
```

---

## 🚀 Production Deployment

### 1. Environment Variables

Production için `.env` dosyası oluşturun:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/ucusyonetim
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_password
JWT_SECRET=your-production-secret-key
```

### 2. Docker Compose Override

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### 3. SSL/HTTPS

Nginx'e SSL sertifikası ekleyin:

```nginx
server {
    listen 443 ssl;
    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ...
}
```

---

## 📞 Destek

Sorun yaşarsanız:
1. Logları kontrol edin
2. GitHub Issues açın
3. Dokümantasyonu inceleyin

**Başarılar!** 🎉
