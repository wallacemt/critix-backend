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
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null){
            var login = tokenService.validationToken(token);
            Optional<Usuario> user = usuarioRepository.findByEmail(login);

            var authentication = new UsernamePasswordAuthenticationToken(user,null,user.get().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request,response);
    }

    //verificar se a requisição está autorizada.
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization"); //Obter cabeçalho de autorização
        if (authHeader != null){
            return authHeader.replace("Bearer","");
        }

        return null;

    }
}
