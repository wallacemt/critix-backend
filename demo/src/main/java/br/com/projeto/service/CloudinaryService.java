package br.com.projeto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

@Service
public class CloudinaryService {
    private final WebClient webClient;

    @Value("${cloudinary.api.name}")
    public String cloudinaryName;

    @Value("${cloudinary.api.key}")
    public String apiKey;

    @Value("${cloudinary.api.secret}")
    public String apiSecret;

    public CloudinaryService(WebClient.Builder webClientBuilder) {
        // Configuração do baseUrl para a API do Cloudinary
        this.webClient = webClientBuilder.baseUrl("https://api.cloudinary.com/v1_1/").build();
    }

    public Mono<Map> fetchImages(String nextCursor, String folderName) {
        String url = String.format("%s/resources/search", cloudinaryName);

        // Codificando manualmente o cabeçalho Authorization (apiKey:apiSecret) em Base64
        String credentials = apiKey + ":" + apiSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        Map<String, Object> requestBody = Map.of(
                "expression", "folder=" + folderName,
                "max_results", 6
        );

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(url)
                        .queryParam("next_cursor", nextCursor != null ? nextCursor : "") // Passando next_cursor se houver
                        .build())
                .headers(headers -> headers.set("Authorization", "Basic " + encodedCredentials)) // Autorização manual em Base64
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new RuntimeException("Erro na requisição para Cloudinary: " + error)));
                })
                .bodyToMono(Map.class);  // Retorna a resposta completa como Map
    }
}