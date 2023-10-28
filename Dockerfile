FROM openjdk:17-oracle
EXPOSE 8080
ADD target/spring-boot-docker.jar spring-boot-docker.jar
ENTRYPOINT ["java", "-jar", "/spring-boot-docker.jar", "--reload", "--host", "0.0.0.0", "--port", "8080"]