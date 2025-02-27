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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    public Page<UsuarioFollowDTO> getFollowing(Long followerId, Pageable pageable, Usuario usuario) {
        Usuario follower = usuarioRepository.findById(followerId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        Page<Follower> followersPage = followerRepository.findByFollower(follower, pageable);

        List<UsuarioFollowDTO> followingDTOs = followersPage.getContent().stream()
                .map(f -> convertToDTO(f.getFollowing(), usuario))
                .collect(Collectors.toList());

        return new PageImpl<>(followingDTOs, pageable, followersPage.getTotalElements());
    }

    public Page<UsuarioFollowDTO> getFollowers(Long userId, Pageable pageable, Usuario follow) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        Page<Follower> followersPage = followerRepository.findByFollowing(usuario, pageable);

        List<UsuarioFollowDTO> followersDTOs = followersPage.getContent().stream()
                .map(f -> convertToDTO(f.getFollower(), follow))
                .collect(Collectors.toList());

        return new PageImpl<>(followersDTOs, pageable, followersPage.getTotalElements());
    }


    public boolean getIsFollow(Long id, Usuario usuario) {
        Usuario following = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        return followerRepository.findByFollowerAndFollowing(usuario, following).isPresent();
    }

    public ResponseEntity<String> follow(Usuario follower, Long followingId) {
        if (follower.getId().equals(followingId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você não pode seguir a si mesmo.");
        }

        Usuario following = usuarioRepository.findById(followingId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        Optional<Follower> existingFollower = followerRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollower.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você já segue este usuário.");
        }

        Follower newFollower = new Follower();
        newFollower.setFollower(follower);
        newFollower.setFollowing(following);
        followerRepository.save(newFollower);

        follower.setFollowings(follower.getFollowings() + 1);
        following.setFollowers(following.getFollowers() + 1);

        usuarioRepository.save(follower);
        usuarioRepository.save(following);

        return ResponseEntity.ok("Usuário seguido com sucesso!");
    }

    public ResponseEntity<String> unfollow(Usuario follower, Long followingId) {
        Usuario following = usuarioRepository.findById(followingId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));

        Optional<Follower> existingFollower = followerRepository.findByFollowerAndFollowing(follower, following);

        if (existingFollower.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você não segue este usuário.");
        }

        followerRepository.delete(existingFollower.get());

        if (follower.getFollowings() != 0) {
            follower.setFollowings(follower.getFollowings() - 1);
        }
        if (following.getFollowers() != 0) {
            following.setFollowers(following.getFollowers() - 1);
        }

        usuarioRepository.save(follower);
        usuarioRepository.save(following);

        return ResponseEntity.ok("Usuário removido da lista de seguidos.");
    }


    private UsuarioFollowDTO convertToDTO(Usuario usuario, Usuario follow ) {
        return new UsuarioFollowDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getImagePath(),
                usuario.equals(follow)
        );
    }

}
