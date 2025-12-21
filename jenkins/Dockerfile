FROM jenkins/jenkins:lts

USER root

# Maven kur
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean

# Node.js kur
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs && \
    apt-get clean

# Docker CLI kur
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean

# Chrome browser ve dependencies kur (Selenium için)
RUN apt-get update && \
    apt-get install -y wget gnupg2 && \
    wget -q -O /usr/share/keyrings/google-chrome-keyring.gpg https://dl-ssl.google.com/linux/linux_signing_key.pub && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google-chrome-keyring.gpg] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable && \
    apt-get install -y xvfb dbus-x11 && \
    apt-get clean

# Selenium için display ayarları
ENV DISPLAY=:99

USER jenkins
