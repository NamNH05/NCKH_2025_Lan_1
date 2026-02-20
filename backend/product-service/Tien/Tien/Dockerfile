# --- GIAI ĐOẠN 1: BUILD (Đóng gói code) ---
# Chọn image Maven có sẵn Java 21 để build
FROM maven:3.9-eclipse-temurin-21 AS build

# Tạo thư mục làm việc trong container
WORKDIR /app

# Copy toàn bộ code dự án vào container
COPY . .

# Chạy lệnh đóng gói (bỏ qua test để build nhanh hơn)
RUN mvn clean package -DskipTests

# --- GIAI ĐOẠN 2: RUN (Chạy ứng dụng) ---
# Chọn image Java 21 bản siêu nhẹ (Slim) để chạy
# Thay thế dòng: FROM openjdk:21-jdk-slim
FROM eclipse-temurin:21-jre-alpine
# Tạo thư mục làm việc
WORKDIR /app

# Copy file .jar đã build xong từ Giai đoạn 1 sang Giai đoạn 2
# Lưu ý: Lệnh này tự tìm file .jar trong thư mục target và đổi tên thành app.jar
COPY --from=build /app/target/*.jar app.jar

# Mở cổng mặc định (Spring Boot thường là 8080)
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]