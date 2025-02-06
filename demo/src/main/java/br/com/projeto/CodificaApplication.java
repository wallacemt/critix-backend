package br.com.projeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class CodificaApplication {



	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		// Define as propriedades do sistema com as variáveis carregadas
		System.setProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL"));
		System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME"));
		System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
		System.setProperty("server.port", dotenv.get("SERVER_PORT"));
		System.setProperty("api.security.token.secret", dotenv.get("API_SECURITY_TOKEN_SECRET"));
		System.setProperty("spring.mail.username", dotenv.get("SPRING_MAIL_USERNAME"));
		System.setProperty("spring.mail.password", dotenv.get("SPRING_MAIL_PASSWORD"));
		System.setProperty("cloudinary.api.name", dotenv.get("CLOUDINARY.API.NAME"));
		System.setProperty("cloudinary.api.key", dotenv.get("CLOUDINARY.API.KEY"));
		System.setProperty("cloudinary.api.secret", dotenv.get("CLOUDINARY.API.SECRET"));
		System.setProperty("google.client.id", dotenv.get("GOOGLE_CLIENT_ID"));

		SpringApplication.run(CodificaApplication.class, args);
	}

}

