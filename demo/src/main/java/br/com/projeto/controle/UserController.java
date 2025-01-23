package br.com.projeto.controle;


import br.com.projeto.dto.BannerProfileDTO;
import br.com.projeto.dto.ImageProfileDTO;
import br.com.projeto.dto.UsuarioDTO;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.service.UsuarioGerenciamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UsuarioGerenciamentoService usuarioGerenciamentoService;

    @GetMapping
    public ResponseEntity<UsuarioDTO> getUser(@AuthenticationPrincipal Usuario usuario){
        UsuarioDTO usuarioDTO = usuarioGerenciamentoService.getUser(usuario.getUsername());

        return  ResponseEntity.ok(usuarioDTO);
    }

    @PutMapping(value = "/profile-image")
    public String putImageProfile(@AuthenticationPrincipal Usuario usuario, @RequestBody ImageProfileDTO imageProfileDTO){
        return usuarioGerenciamentoService.setProfilePath(usuario.getUsername(), imageProfileDTO.getImage());
    }

    @PutMapping(value = "profile-banner")
    public String putBannerProfile(@AuthenticationPrincipal Usuario usuario, @RequestBody BannerProfileDTO bannerProfileDTO){
        return usuarioGerenciamentoService.setBannerProfilePath(usuario.getUsername(), bannerProfileDTO.getBanner());
    }
}
