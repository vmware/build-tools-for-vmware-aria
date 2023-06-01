FROM photon:4.0
RUN tdnf install -y python3 python3-pip
COPY ["requirements", "/tmp/requirements"]
RUN cd /tmp/requirements && pip3 install -r dev.txt
