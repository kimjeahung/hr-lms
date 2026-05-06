FROM jenkins/jenkins:lts

USER root

RUN apt-get update && \
    apt-get install -y docker.io curl && \
  curl -fsSL https://dl.k8s.io/release/stable.txt -o /tmp/k8s_version && \
  curl -fsSL "https://dl.k8s.io/release/$(cat /tmp/k8s_version)/bin/linux/amd64/kubectl" -o /usr/local/bin/kubectl && \
  chmod +x /usr/local/bin/kubectl && \
  rm -f /tmp/k8s_version && \
    usermod -aG root jenkins && \
    mkdir -p /usr/local/lib/docker/cli-plugins && \
    curl -SL https://github.com/docker/compose/releases/download/v2.27.1/docker-compose-linux-x86_64 \
      -o /usr/local/lib/docker/cli-plugins/docker-compose && \
    chmod +x /usr/local/lib/docker/cli-plugins/docker-compose && \
    apt-get clean

USER jenkins