package br.com.projeto.controle;


import br.com.projeto.dto.*;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.service.UsuarioGerenciamentoService;
import br.com.projeto.ultils.PasswordsDoNotMatchException;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UsuarioGerenciamentoService usuarioGerenciamentoService;

    @GetMapping("/{username}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable String username, @AuthenticationPrincipal Usuario usuario) {
        try {
            UsuarioDTO usuarioDTO = usuarioGerenciamentoService.getUserById(username, usuario);
            return ResponseEntity.ok(usuarioDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<UsuarioDTO> getUser(@AuthenticationPrincipal Usuario usuario) {
        UsuarioDTO usuarioDTO = usuarioGerenciamentoService.getUser(usuario.getUsername());
        return ResponseEntity.ok(usuarioDTO);
    }

    @PutMapping(value = "/profile-image")
    public String putImageProfile(@AuthenticationPrincipal Usuario usuario, @RequestBody ImageProfileDTO imageProfileDTO) {
        return usuarioGerenciamentoService.setProfilePath(usuario.getUsername(), imageProfileDTO.getImage());
    }

    @PutMapping(value = "profile-banner")
    public String putBannerProfile(@AuthenticationPrincipal Usuario usuario, @RequestBody BannerProfileDTO bannerProfileDTO) {
        return usuarioGerenciamentoService.setBannerProfilePath(usuario.getUsername(), bannerProfileDTO.getBanner());
    }

    @PutMapping("/profile")
    public ResponseEntity<?> editarPerfil(
            @RequestBody Map<String, Object> updates,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        try {
            UserEditResponseDTO usuarioAtualizado = usuarioGerenciamentoService.editUserInfo(updates, usuarioAutenticado);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro: " + e.getMessage());
        } catch (PasswordsDoNotMatchException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-tier")
    public ResponseEntity<List<UserTopDTO>> getTierRank(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioGerenciamentoService.getTopTierUsers(usuario));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UsuarioSearchDTO>> searchUsers(
            @RequestParam String usernameUser,
            @AuthenticationPrincipal Usuario usuario,
            @PageableDefault(size = 10, sort = "usernameUser", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(usuarioGerenciamentoService.searchUsers(usernameUser, pageable, usuario));
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteUser(@RequestBody DeleteUserDTO request, @AuthenticationPrincipal Usuario usuario) throws InvalidCredentialsException {
        try {
            usuarioGerenciamentoService.deleteUser(usuario, request.getPassword());
            return ResponseEntity.ok(Map.of("message", "Conta excluída com sucesso!"));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }

}
