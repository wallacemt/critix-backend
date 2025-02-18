package br.com.projeto;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodificaApplicationTests {

	@BeforeAll
	static void setup() {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		System.setProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL", "jdbc:mysql://localhost:3306/test_db"));
		System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME", "root"));
		System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD", "root"));
		System.setProperty("server.port", dotenv.get("SERVER_PORT", "8080"));
		System.setProperty("spring.mail.username", dotenv.get("SPRING_MAIL_USERNAME", "teste@email.com"));
		System.setProperty("spring.mail.password", dotenv.get("SPRING_MAIL_PASSWORD", "teste123"));
		System.setProperty("cloudinary.api.name", dotenv.get("CLOUDINARY.API.NAME", "teste1234"));
		System.setProperty("cloudinary.api.key", dotenv.get("CLOUDINARY.API.KEY", "teste1234"));
		System.setProperty("cloudinary.api.secret", dotenv.get("CLOUDINARY.API.SECRET", "teste1234"));
		System.setProperty("google.client.id", dotenv.get("GOOGLE_CLIENT_ID", "teste1234"));
		System.setProperty("api.security.token.secret", dotenv.get("API_SECURITY_TOKEN_SECRET", "test_secret"));
	}

	@Test
	void contextLoads() {
	}
}
