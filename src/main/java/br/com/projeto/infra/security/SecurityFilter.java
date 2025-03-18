package br.com.projeto.infra.security;

import br.com.projeto.models.usuario.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import br.com.projeto.repositorio.UsuarioRepository;
import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            try {
                authenticateUser(token);
            } catch (RuntimeException e) {
                System.out.println("Erro ao autenticar o token: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.replace("Bearer ", "").trim();
        }
        return null;
    }
    private void authenticateUser(String token) {
        var login = tokenService.validationToken(token);
        Optional<Usuario> user = usuarioRepository.findByEmail(login);

        if (user.isPresent()) {
            var authentication = new UsernamePasswordAuthenticationToken(user.get(), null, user.get().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
