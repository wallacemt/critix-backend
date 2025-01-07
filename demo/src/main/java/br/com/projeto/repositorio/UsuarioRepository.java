package br.com.projeto.repositorio;

import br.com.projeto.models.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca um usuário pelo email (retorna Optional para evitar null pointer exceptions)
    Optional<Usuario> findByEmail(String email);

    // Busca um usuário pelo email e código de recuperação de senha
    Optional<Usuario> findByEmailAndCodigoRecuperacaoSenha(String email, String codigoRecuperacaoSenha);

    Optional<Usuario> findByCodigoRecuperacaoSenha(String codigoRecuperacaoSenha);
}
