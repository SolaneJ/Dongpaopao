FROM amazoncorretto:21.0.7

# 将应用程序的 JAR 文件复制到容器中
COPY ["target/pao-gateway-1.0-SNAPSHOT.jar","/pao-gateway.jar"]

CMD ["java","-jar","/pao-gateway.jar"]