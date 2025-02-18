package br.com.projeto.service;

import br.com.projeto.infra.security.TokenService;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.UsuarioRepository;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Service
public class RefreshTokenService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    public String  createAcessTokenFromRefreshToken(String refreshToken) {
        try{
            String username = tokenService.validationToken(refreshToken);

            if(username == null){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "RefreshToken Inválido ou expirado.");
            }

            Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(username);
            if(usuarioOptional.isEmpty()){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado!");
            }
            Usuario usuario = usuarioOptional.get();

            return tokenService.generateToken(usuario);
        } catch (JWTVerificationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "RefreshToken inválido ou expirado.");
        }
    }
}
