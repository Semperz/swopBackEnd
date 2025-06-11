# --- Etapa de build ---
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace


# Copiamos primero únicamente el wrapper y los POM
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -ntp dependency:go-offline


# Ahora el resto del código
COPY src ./src
RUN ./mvnw -ntp -Dmaven.test.skip=true package


# --- Etapa de runtime ---
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]





