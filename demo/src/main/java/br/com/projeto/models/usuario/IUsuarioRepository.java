package br.com.projeto.models.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

public interface IUsuarioRepository extends JpaRepository<Usuario,Long> {
    UserDetails findByLogin(String login);

    @Query("SELECT u FROM usuarios u WHERE u.login = :login")
    Usuario buscarPorLogin(String login);

    Usuario findByLoginAndCodigoRecuperacaoSenha(String login,String codigo);
}
