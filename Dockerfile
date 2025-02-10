FROM openjdk:17-jdk-slim
WORKDIR /app
RUN echo "Current directory:" && pwd
RUN echo "Listing root folder:" && ls -l /
RUN echo "Listing api folder:" && ls -l /api || echo "api folder not found"
RUN echo "Listing target folder:" && ls -l /api/target || echo "target folder not found"
COPY api/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]