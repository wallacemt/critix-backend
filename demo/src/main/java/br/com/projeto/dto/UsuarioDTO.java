package br.com.projeto.dto;

import br.com.projeto.models.usuario.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private String name;
    private String email;
    private String imagePath;
    private String bannerPath;
    private int reviews;
    private int followers;
    private int followings;
    private boolean isUser;
    private String username;


    public UsuarioDTO(Usuario entity, boolean ocultaEmail, boolean isUser) {
        this.id = entity.getId();
        this.name = entity.getNome();
        this.email = ocultaEmail ? null : entity.getEmail();
        this.imagePath = entity.getImagePath();
        this.bannerPath = entity.getBannerPath();
        this.reviews = entity.getReviews();
        this.followers = entity.getFollowers();
        this.followings = entity.getFollowings();
        this.isUser = isUser;
        this.username = entity.getUsernameUser();
    }
}
