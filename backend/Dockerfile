# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Chrome browser ve Selenium dependencies (test için)
RUN apt-get update && \
    apt-get install -y wget gnupg2 && \
    wget -q -O /usr/share/keyrings/google-chrome-keyring.gpg https://dl.google.com/linux/linux_signing_key.pub && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable xvfb dbus-x11 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine

# Alpine için Chrome browser kurulumu
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    xvfb \
    dbus \
    font-noto-emoji \
    && rm -rf /var/cache/apk/*

# Chrome environment variables
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROME_PATH=/usr/lib/chromium/
ENV DISPLAY=:99

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
