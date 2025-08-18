FROM amazoncorretto:21.0.7

# 将应用程序的 JAR 文件复制到容器中
COPY ["target/pao-order-service-1.0-SNAPSHOT.jar","/pao-order-service.jar"]

CMD ["java","-jar","/pao-order-service.jar"]