package br.com.projeto.service;

import br.com.projeto.dto.UsuarioFollowDTO;
import br.com.projeto.models.followers.Follower;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.repositorio.FollowerRepository;
import br.com.projeto.repositorio.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FollowerService {
    @Autowired
    FollowerRepository followerRepository;
    @Autowired
    UsuarioRepository usuarioRepository;


    public Page<UsuarioFollowDTO> getFollowing(Long followerId, Pageable pageable) {
        Usuario follower = usuarioRepository.findById(followerId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        Page<Follower> followersPage = followerRepository.findByFollower(follower, pageable);

        List<UsuarioFollowDTO> followingDTOs = followersPage.getContent().stream()
                .map(f -> convertToDTO(f.getFollowing()))
                .collect(Collectors.toList());

        return new PageImpl<>(followingDTOs, pageable, followersPage.getTotalElements());
    }


    public void follow(Usuario follower, Long follwing) {
        Usuario follwingOp = usuarioRepository.findById(follwing).orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado!"));
        Optional<Follower> existingFollower = followerRepository.findByFollowerAndFollowing(follower, follwingOp);
        if (!existingFollower.isPresent()) {
            Follower newFollower = new Follower();
            newFollower.setFollower(follower);
            newFollower.setFollowing(follwingOp);
            followerRepository.save(newFollower);

            follower.setFollowings(follower.getFollowings() + 1);
            follwingOp.setFollowers(follwingOp.getFollowers() + 1);

            usuarioRepository.save(follower);
            usuarioRepository.save(follwingOp);
            throw new DuplicateKeyException("Ja segue esse Usuario");
        }
    }

    public void unfollow(Usuario follower, Long follwing) {
        Usuario follwingOp = usuarioRepository.findById(follwing).orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado!"));
        Optional<Follower> existingFollower = followerRepository.findByFollowerAndFollowing(follower, follwingOp);
        if (existingFollower.isPresent()) {
            followerRepository.delete(existingFollower.get());
            follower.setFollowings(follower.getFollowings() - 1);
            follwingOp.setFollowers(follwingOp.getFollowers() - 1);

            usuarioRepository.save(follower);
            usuarioRepository.save(follwingOp);
        } else {
            throw new EntityNotFoundException("Não segue esse Usuario!");
        }
    }


    private UsuarioFollowDTO convertToDTO(Usuario usuario) {
        return new UsuarioFollowDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getImagePath()
        );
    }

}
