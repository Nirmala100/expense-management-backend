#FROM gradle:7-jdk11-alpine AS build
FROM gradle:7-jdk11-jammy AS build
COPY --chown=gradle:gradle ./ /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon
RUN unzip -o /home/gradle/src/build/distributions/EMS-shadow-1.0-SNAPSHOT.zip -d /home/gradle/src/build/distributions/

FROM openjdk:11.0-jre-slim
ENV ENVIRONMENT=production
EXPOSE 8081
WORKDIR /app
COPY --from=build /home/gradle/src/build/distributions/EMS-shadow-1.0-SNAPSHOT/ /app/
ENTRYPOINT ["/app/bin/EMS"]
