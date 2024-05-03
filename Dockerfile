# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk-jammy as base

# The Maven image only has Jammy from JDK 21+, so using Focal instead
FROM maven:3.9.6-eclipse-temurin-17-focal as build
COPY . /app
RUN apt-get update && \
  apt-get --yes --no-install-recommends install curl && \
  curl -sL https://deb.nodesource.com/setup_18.x | bash && \
  apt-get --yes --no-install-recommends install nodejs
RUN cd /app && mvn package

FROM base AS final

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

COPY --from=build /app/build/snowmail-*.jar /app/snowmail.jar

WORKDIR "/app"
EXPOSE 8025
CMD [ "java", "-jar", "/app/snowmail.jar" ]
