package br.com.projeto.controle;

import br.com.projeto.models.Pessoa;
import br.com.projeto.repositorio.Repositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controle {

    @Autowired
    private Repositorio repositorio;

    @PostMapping("/cadastrar")
    public Pessoa cadastrar(@RequestBody Pessoa pessoa){
        return repositorio.save(pessoa);
    }

    @GetMapping("")
    public String mensagem(){
        return "Hello word";
    }

    @GetMapping("/boasVindas/{nome}")
    public String boasVindas(@PathVariable String nome){
        return "Seja bem vindo "+nome;
    }

    @PostMapping("/pessoa")
    public Pessoa pessoa(@RequestBody Pessoa pessoa){
        return pessoa;
    }
}
