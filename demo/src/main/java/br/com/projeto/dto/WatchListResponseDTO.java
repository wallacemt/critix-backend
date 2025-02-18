package br.com.projeto.dto;

import br.com.projeto.models.watchlist.WatchList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class WatchListResponseDTO {
    private Long id;
    private Long mediaId;
    private String mediaType;
    private String mediaStatus;
    private LocalDateTime updatedAt;


    public WatchListResponseDTO(WatchList watchList) {
        this.id = watchList.getId();
        this.mediaId = watchList.getMediaId();
        this.mediaType = watchList.getMediaType().name();
        this.mediaStatus = watchList.getMediaStatus().name();
        this.updatedAt = watchList.getUpdatedAt();
    }
}

