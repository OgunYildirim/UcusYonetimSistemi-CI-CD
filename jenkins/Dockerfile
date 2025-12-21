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

USER jenkins
