FROM openjdk:17-alpine
EXPOSE 8098
ADD target/resource-tracker.jar  resource-tracker.jar
ENTRYPOINT ["java","-jar","/resource-tracker.jar"]
