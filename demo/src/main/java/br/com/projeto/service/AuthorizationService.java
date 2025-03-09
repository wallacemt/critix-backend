package br.com.projeto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import br.com.projeto.repositorio.UsuarioRepository;
import br.com.projeto.models.usuario.*;

import java.util.Optional;

@Service
    public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recuperando o Optional de Usuario
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        // Verifica se o usuário foi encontrado
        Usuario usuario = usuarioOptional.orElseThrow(() ->
                new UsernameNotFoundException("Usuário não encontrado com o email: " + email)
        );

        // Retorna o Usuario, que agora precisa implementar UserDetails
        return usuario;
    }

}
