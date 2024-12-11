package com.example.Artifact.ola_mundo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/teste")
    public String olamundo(){
        return "Olá Mundo!";
    }
}
