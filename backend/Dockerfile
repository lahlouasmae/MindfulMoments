FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copier le JAR
COPY backend/target/*.jar app.jar

# Créer le dossier uploads sans le copier
RUN mkdir -p /app/uploads/videos

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]