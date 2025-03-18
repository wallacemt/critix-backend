package br.com.projeto.service;

import br.com.projeto.dto.WatchListRequestDTO;
import br.com.projeto.dto.WatchListResponseDTO;
import br.com.projeto.dto.WatchListUpdateRequestDTO;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.models.watchlist.MediaStatus;
import br.com.projeto.models.watchlist.MediaType;
import br.com.projeto.models.watchlist.WatchList;
import br.com.projeto.repositorio.UsuarioRepository;
import br.com.projeto.repositorio.WatchListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WatchListService {
    @Autowired
    private WatchListRepository watchListRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    //Pega a watchlist do usuario Autenticado
    public Page<WatchListResponseDTO> getWatchlistByStatus(String userName, MediaStatus status, Pageable pageable){
        Optional<Usuario> usuario = usuarioRepository.findByEmail(userName);
        Usuario usuarioEntity = usuario.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para o e-mail: " + userName));
        Pageable sortedByUpdatedAtDesc = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );
        return  watchListRepository.findByStatus(usuarioEntity.getId(), status, sortedByUpdatedAtDesc);
    }

    //Adiciona um item á watchlist
    public WatchList addToWatchList(String username, WatchListRequestDTO request){
        WatchList item = new WatchList();
        Optional<Usuario> usuario = usuarioRepository.findByEmail(username);
        Usuario usuarioEntity = usuario.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para o e-mail: " + username));

        WatchList watchlistQuery = watchListRepository.findByMedia(request.getMediaId(), request.getMediaType(), usuarioEntity.getId());

        if(watchlistQuery != null) {
            throw  new DuplicateKeyException("Elemento com mesmo id e tipo já adicionado na watchlist");
        }

        item.setUser(usuarioEntity);
        item.setMediaId(request.getMediaId());
        item.setMediaType(request.getMediaType());
        item.setMediaStatus(request.getMediaStatus());
        item.setCreateAt(LocalDateTime.now());

        return watchListRepository.save(item);
    }

    //Atualiza o status de um item na watchList
    public WatchList updateWatchListItem(Long id, WatchListUpdateRequestDTO update){
        WatchList item = watchListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado na watchlist"));
        item.setMediaStatus(update.getMediaStatus());
        return  watchListRepository.save(item);
    }

    //Remove um item da WatchList
    public void removeFromWatchList(Long mediaId, MediaType mediaType, Long id){
        if(mediaType == null){
            throw  new IllegalArgumentException("Tipo de mídia invalido");
        }
        WatchList query = watchListRepository.findByMedia(mediaId, mediaType, id);
        if(query == null){
            throw new EntityNotFoundException("Item não encontrado na watchlist");
        }
        watchListRepository.delete(query);
    }

    //Verifica se uma média já esta na watchlist
    public boolean isOnTheWatchlist(Long mediaId, MediaType mediaType, Long userId) {
        if (mediaType == null) {
            throw new IllegalArgumentException("Tipo de mídia inválido");
        }
        return watchListRepository.findByMedia(mediaId, mediaType, userId) != null;
    }

}
