package br.com.projeto.models.usuario;

import br.com.projeto.models.comment.Comment;
import br.com.projeto.models.followers.Follower;
import br.com.projeto.models.notifications.Notification;
import br.com.projeto.models.review.Review;
import br.com.projeto.models.review.ReviewLike;
import br.com.projeto.models.watchlist.WatchList;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Table(name = "usuarios") // Nome da tabela no banco
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, unique = true, length = 255)
    @Email(message = "O email deve ser válido")
    private String email;

    @Column(nullable = false, length = 255)
    @Size(min = 8, message = "A senha deve ter pelo menos 8 caracteres.")
    private String senha;

    @Column(name = "image_path")
    private String imagePath; // URL da foto de perfil, pode ser nulo

    @Column(name = "banner_path")
    private String bannerPath; // URL da capa de perfil, pode ser nulo


    @Column(name = "data_cadastro", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date(); // Inicializa com a data atual

    @Column(nullable = false)
    private int reviews = 0; // Quantidade de resenhas feitas

    @Column(nullable = false)
    private int followers = 0; // Quantidade de seguidores

    @Column(nullable = false)
    private int followings = 0; // Quantidade de pessoas que o usuário segue

    @Column(name = "codigo_recuperacao_senha")
    private String codigoRecuperacaoSenha;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvioCodigo;

    public Usuario(Long id, String nome, String email, String senha, String imagePath, String bannerPath, int reviews, int followers, int followings) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.imagePath = imagePath;
        this.bannerPath = bannerPath;
        this.dataCadastro = new Date();
        this.reviews = reviews;
        this.followers = followers;
        this.followings = followings;
    }

    /**
     * Atualiza o código de recuperação de senha e a data de envio.
     *
     * @param codigo Código de recuperação gerado
     */
    public void atualizarCodigoRecuperacaoSenha(String codigo) {
        this.codigoRecuperacaoSenha = codigo;
        this.dataEnvioCodigo = new Date(); // Atualiza a data para o momento atual
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email; // Alterado para usar email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewss;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLike> likes;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follower> following;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follower> followerss;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchList> watchlist;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> destination;

    @OneToMany(mappedBy = "remetente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> remetente;
}
