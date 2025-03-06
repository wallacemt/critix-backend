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

//    @Override
//    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
//        //Tenta buscar pelo username
//        Optional<Usuario> usuarioOptional = usuarioRepository.findByUsernameUser(input);
//
//        if(usuarioOptional.isPresent()){
//            Usuario usuario = usuarioOptional.get();
//            return usuario;
//        }
//
//        //Se não encontrar pelo username, tenta buscar pelo email
//        usuarioOptional = usuarioRepository.findByEmail(input);
//
//        if (usuarioOptional.isPresent()){
//            Usuario usuario = usuarioOptional.get();
//            return usuario;
//        }
//
//        // Se não encontrar pelo username nem pelo email, lança exceção
//        throw new UsernameNotFoundException("Usuário não encontrado com username ou email: "+input);
//
//    }

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
