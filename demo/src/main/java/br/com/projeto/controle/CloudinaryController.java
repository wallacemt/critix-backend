package br.com.projeto.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import br.com.projeto.service.CloudinaryService;

import java.util.Map;
import java.util.Optional;

@RestController
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/api/images")
    public Mono<Map> getImages(@RequestParam(value = "next_cursor", required = false) String nextCursor, @RequestParam("folder_name") String folderName) {
        return cloudinaryService.fetchImages(nextCursor, folderName); // Passa o next_cursor para o serviço
    }
}


