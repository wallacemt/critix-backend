package br.com.projeto.controle;


import br.com.projeto.dto.UsuarioDTO;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.service.UsuarioGerenciamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UsuarioGerenciamentoService usuarioGerenciamentoService;

    @GetMapping
    public ResponseEntity<UsuarioDTO> getUser(@AuthenticationPrincipal Usuario usuario){
        UsuarioDTO usuarioDTO = usuarioGerenciamentoService.getUser(usuario.getUsername());
        System.out.println(usuarioDTO);
        return  ResponseEntity.ok(usuarioDTO);
    }
}
