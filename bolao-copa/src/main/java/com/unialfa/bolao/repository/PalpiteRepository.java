package com.unialfa.bolao.repository;

import com.unialfa.bolao.model.Palpite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PalpiteRepository extends JpaRepository<Palpite, Long> {
    List<Palpite> findByPartidaId(Long partidaId);
    List<Palpite> findByUsuarioId(Long usuarioId);
    List<Palpite> findByUsuarioEmailOrderByPartidaDataHoraAsc(String email);
    Optional<Palpite> findByUsuarioIdAndPartidaId(Long usuarioId, Long partidaId);
    boolean existsByUsuarioIdAndPartidaId(Long usuarioId, Long partidaId);
    long countByPartidaId(Long partidaId);
}
