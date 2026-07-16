## AdoptOpenJDK 停止发布 OpenJDK 二进制，而 Eclipse Temurin 是它的延伸，提供更好的稳定性
## 感谢复旦核博士的建议！灰子哥，牛皮！
FROM eclipse-temurin:17-jre

## 创建目录，并使用它作为工作目录
RUN mkdir -p /yudao-server
WORKDIR /yudao-server
## 将后端项目的 Jar 文件，复制到镜像中
COPY ./yudao-server.jar app.jar

## 设置 TZ 时区
ENV TZ=Asia/Shanghai
## JAVA_OPTS 环境变量，由 docker-compose.yml 或 docker run -e 覆盖
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dspring.profiles.active=prod -Djava.security.egd=file:/dev/./urandom -XX:+UseG1GC"

## 暴露后端项目的 48080 端口
EXPOSE 48080

## 启动后端项目
CMD java ${JAVA_OPTS} -jar app.jar
