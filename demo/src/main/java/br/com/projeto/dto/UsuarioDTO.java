package br.com.projeto.dto;

import br.com.projeto.models.usuario.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@Getter
public class UsuarioDTO {
    private Long id;
    private String name;
    private String email;
    private String imagePath;
    private String bannePath;
    private int reviews;
    private int followers;
    private int followings;

    public UsuarioDTO(Usuario entity) {
        this.id = entity.getId();
        this.name = entity.getNome();
        this.email = entity.getEmail();
        this.imagePath = entity.getImagePath();
        this.bannePath = entity.getBannerPath();
        this.reviews = entity.getReviews();
        this.followers = entity.getFollowers();
        this.followings = entity.getFollowings();
    }
}
