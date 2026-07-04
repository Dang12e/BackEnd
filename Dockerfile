# Bước 1: Build mã nguồn bằng Maven với JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Bước 2: Khởi chạy ứng dụng với JDK 21 tinh gọn
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Cấu hình cực kỳ quan trọng cho Render Free:
# -Xmx256m: Giới hạn tối đa bộ nhớ Heap chỉ chiếm 256MB RAM (giúp tổng RAM container luôn nằm dưới mức 512MB của Render)
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]
