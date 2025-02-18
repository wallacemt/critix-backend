package br.com.projeto.dto;

import br.com.projeto.models.watchlist.MediaStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WatchListUpdateRequestDTO {
    private MediaStatus mediaStatus;
}
