package br.com.projeto.models.usuario;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity(name = "usuarios") //Cria a tabela no banco
@Table(name = "usuarios")   //Nome da tabela no banco
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id //Especifica o atributo com chave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Especifica a coluna como auto-increment
    private Long id;
    private String login;
    private String senha;
    private String nome;
    private String codigoRecuperacaoSenha;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvioCodigo;

    public Usuario(String codigoRecuperacaoSenha,Date dataEnvioCodigo){
        this.codigoRecuperacaoSenha=codigoRecuperacaoSenha;
        this.dataEnvioCodigo = dataEnvioCodigo;
    }

    public Usuario(String login,String senha,String nome){
        this.login = login;
        this.senha = senha;
        this.nome = nome;
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
        return login;
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
}
