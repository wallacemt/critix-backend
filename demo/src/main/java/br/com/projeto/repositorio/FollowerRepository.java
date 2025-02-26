package br.com.projeto.repositorio;

import br.com.projeto.models.followers.Follower;
import br.com.projeto.models.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    Optional<Follower> findByFollowerAndFollowing(Usuario follower, Usuario following);

    @Query("SELECT f FROM Follower f JOIN FETCH f.following WHERE f.follower = :follower")
    Page<Follower> findByFollower(Usuario follower, Pageable pageable);


}
