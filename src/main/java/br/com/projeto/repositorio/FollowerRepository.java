package br.com.projeto.repositorio;

import br.com.projeto.models.followers.Follower;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    Optional<Follower> findByFollowerAndFollowing(Usuario follower, Usuario following);

    @Query("SELECT f FROM Follower f JOIN FETCH f.following WHERE f.follower = :follower")
    Page<Follower> findByFollower(Usuario follower, Pageable pageable);

    @Query("SELECT f FROM Follower f JOIN FETCH f.follower WHERE f.following = :usuario")
    Page<Follower> findByFollowing(Usuario usuario, Pageable pageable);

    // Conta quantos seguidores um usuário tem
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.following.id = :usuarioId")
    int countFollowers(@Param("usuarioId") Long usuarioId);

    // Conta quantas pessoas um usuário está seguindo
    @Query("SELECT COUNT(f) FROM Follower f WHERE f.follower.id = :usuarioId")
    int countFollowing(@Param("usuarioId") Long usuarioId);
}
