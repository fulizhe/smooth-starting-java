FROM mcr.microsoft.com/devcontainers/java:8

# 配置apt镜像源
RUN echo "deb https://mirrors.aliyun.com/debian/ bullseye main non-free contrib" > /etc/apt/sources.list.d/aliyun.list && \
    echo "deb https://mirrors.aliyun.com/debian-security/ bullseye-security main" >> /etc/apt/sources.list.d/aliyun.list && \
    echo "deb https://mirrors.aliyun.com/debian/ bullseye-updates main non-free contrib" >> /etc/apt/sources.list.d/aliyun.list

# 安装额外的包
RUN apt-get update && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
        git \
        curl \
        wget && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 配置Maven镜像源
COPY settings.xml /usr/share/maven/ref/
ENV MAVEN_CONFIG=/usr/share/maven/ref

# 设置工作目录
WORKDIR /workspace

# 设置默认shell
ENV SHELL=/bin/bash