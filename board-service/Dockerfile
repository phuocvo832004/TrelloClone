# Sử dụng image Java 22
FROM openjdk:22

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file jar của ứng dụng vào container
COPY target/board-service-0.0.1-SNAPSHOT.jar /app/board-service.jar

# Mở cổng mà ứng dụng sẽ lắng nghe
EXPOSE 8082

# Chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "board-service.jar"]
