FROM gitlab/gitlab-runner:latest

RUN apt-get update
RUN apt-get install -y curl
RUN apt-get install -y openjdk-17-jdk
RUN apt-get install -y software-properties-common
RUN apt-get install -y maven

# Install Node.js (npm is included in the package)
RUN curl -fsSL https://deb.nodesource.com/setup_14.x | bash -
RUN apt-get install -y nodejs

# Print the versions
RUN node -v
RUN npm -v
RUN mvn -v
RUN java --version

# Clean up the package lists to reduce image size
RUN rm -rf /var/lib/apt/lists/* 
