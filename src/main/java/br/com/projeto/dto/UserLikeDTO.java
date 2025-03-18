package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLikeDTO {
    private Long id;
    private String nome;
    private String username;
    private String image;
    private boolean isUser;
}
