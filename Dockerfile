# -------- Etapa de build --------
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace
COPY . .
RUN ./mvnw -ntp -Dmaven.test.skip=true package


# -------- Imagen final --------
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]





