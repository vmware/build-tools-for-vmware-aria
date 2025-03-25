FROM gitlab/gitlab-runner:latest

RUN apt-get update
RUN apt-get install -y curl openjdk-17-jdk software-properties-common maven

# Install Node.js (npm is included in the package)
RUN curl -fsSL https://fnm.vercel.app/install | bash
RUN fnm install 22

# Clean up the package lists to reduce image size
RUN rm -rf /var/lib/apt/lists/* 
