package br.com.projeto.dto;

import br.com.projeto.models.usuario.Usuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@Getter
@Setter
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
        this(entity,false);
    }

    public UsuarioDTO(Usuario usuario, boolean ocultaEmail) {
        this.id = usuario.getId();
        this.name = usuario.getNome();
        this.email = ocultaEmail ? null : usuario.getEmail();// Oculta email se solicitado
        this.imagePath = usuario.getImagePath();
        this.bannePath = usuario.getBannerPath();
        this.reviews = usuario.getReviews();
        this.followers = usuario.getFollowers();
        this.followings = usuario.getFollowings();
    }
}
