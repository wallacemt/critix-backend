package br.com.projeto.repositorio;

import br.com.projeto.dto.WatchListResponseDTO;
import br.com.projeto.models.usuario.Usuario;
import br.com.projeto.models.watchlist.MediaStatus;
import br.com.projeto.models.watchlist.MediaType;
import br.com.projeto.models.watchlist.WatchList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchListRepository  extends JpaRepository<WatchList, Long> {
    Page<WatchListResponseDTO> findByUser(Usuario usuario, Pageable pageable);

    //Pega todos os itens da watchlist com um status especifico
    @Query("SELECT w FROM WatchList w WHERE w.user.id = :userId AND w.mediaStatus = :mediaStatus ORDER BY w.updatedAt DESC")
    Page<WatchListResponseDTO> findByStatus(@Param("userId") Long userId, @Param("mediaStatus") MediaStatus mediaStatus, Pageable pageable);

    //verifica se um item esta na watchlist
    @Query("SELECT w FROM WatchList w WHERE w.mediaId = :mediaId AND w.mediaType = :mediaType AND w.user.id = :userId")
    WatchList findByMedia(@Param("mediaId") Long mediaId, @Param("mediaType") MediaType mediaType, Long userId);

}
