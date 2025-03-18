package br.com.projeto.controle;

import br.com.projeto.dto.WatchListRequestDTO;
import br.com.projeto.dto.WatchListResponseDTO;
import br.com.projeto.dto.WatchListUpdateRequestDTO;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.models.watchlist.MediaStatus;
import br.com.projeto.models.watchlist.MediaType;
import br.com.projeto.models.watchlist.WatchList;
import br.com.projeto.service.WatchListService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/watchlist")
public class WatchListController {
    @Autowired
    private WatchListService watchListService;

    @GetMapping("/{status}")
    public ResponseEntity<Page<WatchListResponseDTO>> getWathListByStatus(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable MediaStatus status,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(watchListService.getWatchlistByStatus(usuario.getUsername(), status, pageable));
    }

    @GetMapping("/{mediaType}/{mediaId}")
    public ResponseEntity<?> isOnTheWatchlist(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable MediaType mediaType,
            @PathVariable Long mediaId) {
        try {
            boolean exists = watchListService.isOnTheWatchlist(mediaId, mediaType, usuario.getId());
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tipo de mídia inválido");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno");
        }
    }

    @PostMapping()
    public ResponseEntity<?> addToWatchList(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody WatchListRequestDTO request) {
        try {
            WatchList item = watchListService.addToWatchList(usuario.getUsername(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Item adicionado com sucesso!");
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Elemento já adicionado à WatchList");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno no servidor");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WatchListResponseDTO> putToWatchList(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody WatchListUpdateRequestDTO update,
            @PathVariable Long id){
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new WatchListResponseDTO(watchListService.updateWatchListItem(id, update)));
    }

    @DeleteMapping("/{mediaType}/{mediaId}")
    public ResponseEntity<?> removeFromWatchList(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable MediaType mediaType,
            @PathVariable Long mediaId){
        try {
            watchListService.removeFromWatchList(mediaId, mediaType, usuario.getId());
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Item não encontrado");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tipo de mídia inválido");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno");
        }
    }
}
