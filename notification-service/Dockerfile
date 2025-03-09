# Sử dụng image Java 22
FROM openjdk:22

# Đặt thư mục làm việc trong container
WORKDIR /app

# Sao chép file jar của ứng dụng vào container
COPY target/notification-service-0.0.1-SNAPSHOT.jar /app/notification-service.jar

# Mở cổng mà ứng dụng sẽ lắng nghe
EXPOSE 8083

# Chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "notification-service.jar"]
