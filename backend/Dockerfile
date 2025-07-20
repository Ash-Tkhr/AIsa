FROM openjdk:17-jdk-slim

WORKDIR /app

# Maven Wrapperをコピー
COPY mvnw .
COPY .mvn .mvn

# pom.xmlをコピー
COPY pom.xml .

# 依存関係をダウンロード
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# ソースコードをコピー
COPY src src

# アプリケーションをビルド
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/aisa-backend-0.0.1-SNAPSHOT.jar"] 