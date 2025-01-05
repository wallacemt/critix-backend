# Usar uma imagem do OpenJDK com JAR embutido
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado para a imagem
COPY target/critix.jar app.jar

# Expor a porta que a aplicação usará
EXPOSE 8081

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
