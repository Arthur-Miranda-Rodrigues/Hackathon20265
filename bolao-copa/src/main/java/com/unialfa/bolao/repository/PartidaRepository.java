package com.unialfa.bolao.repository;

import com.unialfa.bolao.model.Partida;
import com.unialfa.bolao.model.StatusPartida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {
    long countByStatusNot(StatusPartida status);
    List<Partida> findByDataHoraAfterOrderByDataHoraAsc(LocalDateTime dataHora);
}
