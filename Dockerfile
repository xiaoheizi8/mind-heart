# 心域后端 Dockerfile
FROM maven:3.9.5-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制pom文件
COPY pom.xml .
COPY mind-realm-api/pom.xml mind-realm-api/
COPY mind-realm-common/pom.xml mind-realm-common/
COPY mind-realm-diary/pom.xml mind-realm-diary/
COPY mind-realm-emotion/pom.xml mind-realm-emotion/
COPY mind-realm-core/pom.xml mind-realm-core/
COPY mind-realm-warning/pom.xml mind-realm-warning/
COPY mind-realm-admin/pom.xml mind-realm-admin/

# 复制源代码
COPY mind-realm-api/src mind-realm-api/src
COPY mind-realm-common/src mind-realm-common/src
COPY mind-realm-diary/src mind-realm-diary/src
COPY mind-realm-emotion/src mind-realm-emotion/src
COPY mind-realm-core/src mind-realm-core/src
COPY mind-realm-warning/src mind-realm-warning/src
COPY mind-realm-admin/src mind-realm-admin/src

# 构建JAR包
RUN mvn clean package -DskipTests -pl mind-realm-api -am

# 运行镜像
FROM eclipse-temurin:17-jre-alpine

# 设置时区
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo 'Asia/Shanghai' > /etc/timezone

# 创建应用目录
WORKDIR /app

# 复制JAR包
COPY --from=builder /app/mind-realm-api/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx1024m", "app.jar"]
