package br.com.projeto.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long reviewId;
    private String content;
    private LocalDateTime dataCriacao;
    private LocalDateTime updatedAt;
    private  Boolean isOwner;
}
