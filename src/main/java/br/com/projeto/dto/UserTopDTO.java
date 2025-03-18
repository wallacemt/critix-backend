package br.com.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTopDTO {
    private Long id;
    private String username;
    private String image;
    private int reviewCount;
    private int followCount;
    private int totalLikes;
    private boolean isUser;
}
