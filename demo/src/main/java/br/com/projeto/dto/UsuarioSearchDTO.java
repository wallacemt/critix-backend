package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioSearchDTO {
        private Long id;
        private String username;
        private String profileImage;
        private boolean isUser;
}
