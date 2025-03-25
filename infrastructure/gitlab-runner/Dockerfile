FROM gitlab/gitlab-runner:latest

ARG MAVEN_VERSION=3.9.9

RUN apt-get update
RUN apt-get install -y curl openjdk-17-jdk software-properties-common unzip

RUN wget https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -P /tmp \
    && tar xf /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /opt \
    && ln -sf /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/bin/mvn \
    && rm -rf /tmp/apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Install Node.js (npm is included in the package)
RUN curl -fsSL https://fnm.vercel.app/install | bash
RUN fnm install 22

# Clean up the package lists to reduce image size
RUN rm -rf /var/lib/apt/lists/* 
