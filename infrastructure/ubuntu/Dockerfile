FROM ubuntu:bionic

ADD https://github.com/Yelp/dumb-init/releases/download/v1.0.2/dumb-init_1.0.2_amd64 /usr/bin/dumb-init
RUN chmod +x /usr/bin/dumb-init

RUN apt-get update -y && \
    apt-get upgrade -y && \
    apt-get install -y vim && \
    apt-get install -y dnsutils && \
    apt-get install -y iputils-ping && \
    apt-get install -y curl && \
    apt-get install -y openjdk-8-jre-headless && \
    apt-get install -y openjdk-8-jdk-headless && \
    apt-get install -y maven && \
    curl --retry 3 -L https://packages.gitlab.com/install/repositories/runner/gitlab-ci-multi-runner/script.deb.sh | bash && \
    apt-get install -y gitlab-ci-multi-runner && \
    curl --retry 3 -L https://deb.nodesource.com/setup_10.x | bash && \
    apt-get install -y nodejs    

ADD entrypoint /
RUN chmod +x /entrypoint

RUN install -d -o gitlab-runner -g gitlab-runner /home/gitlab-runner

VOLUME ["/etc/gitlab-runner", "/home/gitlab-runner"]
ENTRYPOINT ["/usr/bin/dumb-init", "/entrypoint"]
CMD ["run", "--user=gitlab-runner", "--working-directory=/home/gitlab-runner"]