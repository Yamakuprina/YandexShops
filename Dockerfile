FROM openjdk:17
ADD target/yandex-shops.jar yandex-shops.jar
ENTRYPOINT ["java", "-jar","yandex-shops.jar"]
EXPOSE 80