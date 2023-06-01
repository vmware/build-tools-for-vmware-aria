FROM photon:4.0
RUN tdnf install -y nodejs
COPY ["package.json", "/tmp"]
RUN cd /tmp && npm install
