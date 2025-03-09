package br.com.projeto.repositorio;

import br.com.projeto.models.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca um usuário pelo email (retorna Optional para evitar null pointer exceptions)
    Optional<Usuario> findByEmail(String email);

    // Busca um usuário pelo email e código de recuperação de senha
    Optional<Usuario> findByEmailAndCodigoRecuperacaoSenha(String email, String codigoRecuperacaoSenha);

    Optional<Usuario> findByCodigoRecuperacaoSenha(String codigoRecuperacaoSenha);

    // Busca o usuário pelo seu username
    Optional<Usuario> findByUsernameUser(String username);

    //Retorna o top 3 usuarios
    @Query("""
        SELECT u, 
               u.reviews AS totalReviews, 
               u.followers AS totalFollowers, 
               COALESCE(SUM(CASE WHEN rl.likeType = 'like' THEN 1 ELSE 0 END), 0) AS totalLikes
        FROM Usuario u
        LEFT JOIN Review r ON u.id = r.usuario.id
        LEFT JOIN ReviewLike rl ON r.id = rl.review.id
        GROUP BY u.id
        ORDER BY (u.reviews + u.followers + COALESCE(SUM(CASE WHEN rl.likeType = 'like' THEN 1 ELSE 0 END), 0)) DESC
        LIMIT 3
    """)
    List<Object[]> findTopTierUsuarios();
}
