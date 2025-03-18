package br.com.projeto.dto;

import br.com.projeto.models.watchlist.MediaStatus;
import br.com.projeto.models.watchlist.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WatchListRequestDTO {
    private Long mediaId;
    private MediaType mediaType;
    private MediaStatus mediaStatus;
}
