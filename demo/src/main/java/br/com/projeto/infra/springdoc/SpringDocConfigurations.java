package br.com.projeto.infra.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Informando ao Swagger que a API utiliza autenticação por token JWT (JSON Web Token) e que a interface do Swagger deve fornecer um campo para que os usuários possam inserir seus tokens e testar endpoints protegidos
@Configuration
public class SpringDocConfigurations {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                                .title("Critix API")
                        .description("API Rest da aplicação Critix, contendo as funcionalidades de CRUD da plataforma de filmes ")
                        .contact(new Contact()
                                .name("Time Backend")
                                .email("critixcorp@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://voll.med/api/licenca")));
    }
}
