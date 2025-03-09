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
    Long id;
    String username;
    String image;
    int reviewCount;
    int followCount;
    int totalLikes;
}
