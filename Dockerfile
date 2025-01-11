# Usar uma imagem oficial do Maven para construir o projeto
FROM maven:3.8.7-openjdk-17 AS builder
WORKDIR /app

# Copiar o arquivo pom.xml e baixar dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar o código fonte e construir o projeto
COPY src ./src
RUN mvn clean package -DskipTests

# Usar uma imagem mais leve para rodar a aplicação
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar o .jar gerado no estágio anterior
COPY --from=builder /app/target/critix.jar app.jar

# Expor a porta e executar o .jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
