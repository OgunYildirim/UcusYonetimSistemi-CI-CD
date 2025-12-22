# 1. Aşama: Build Stage (Sadece JAR üretimi)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Aşama: Run Stage (Uygulamanın çalışacağı ve Selenium testlerinin koşacağı yer)
FROM eclipse-temurin:17-jdk-jammy

USER root

# Chrome ve Selenium için gerekli bağımlılıklar
RUN apt-get update && apt-get install -y \
    wget \
    gnupg2 \
    curl \
    xvfb \
    dbus-x11 \
    libgconf-2-4 \
    libnss3 \
    libxss1 \
    libasound2 \
    fonts-liberation \
    libgbm1 \
    --no-install-recommends && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /etc/apt/keyrings/google-chrome.gpg && \
    echo "deb [arch=amd64 signed-by=/etc/apt/keyrings/google-chrome.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Maven kurulumu (Konteyner içinde test koşabilmek için)
RUN apt-get update && apt-get install -y maven && apt-get clean

# Ortam Değişkenleri
ENV CHROME_BIN=/usr/bin/google-chrome
ENV DISPLAY=:99

WORKDIR /app

# --- KRİTİK EKLEME: Maven'ın projeyi tanıması için kaynak kodları kopyalıyoruz ---
COPY --from=build /app/pom.xml .
COPY --from=build /app/src ./src
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Xvfb sanal ekranını arka planda başlatıp uygulamayı çalıştıran komut
ENTRYPOINT ["sh", "-c", "Xvfb :99 -screen 0 1280x1024x24 & java -jar app.jar"]