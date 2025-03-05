package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEditResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String token;
    private String refreshToken;
}
