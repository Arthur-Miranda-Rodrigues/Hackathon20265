package com.unialfa.bolao.repository;

import com.unialfa.bolao.model.Partida;
import com.unialfa.bolao.model.StatusPartida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PartidaRepository extends JpaRepository<Partida, Long> {
    long countByStatusNot(StatusPartida status);
    List<Partida> findByDataHoraAfterOrderByDataHoraAsc(LocalDateTime dataHora);

    @Modifying
    @Query("update Partida p set p.status = :novo, p.atualizadoEm = CURRENT_TIMESTAMP " +
           "where p.status = :atual and p.dataHora <= :agora")
    int atualizarStatusPorHorario(StatusPartida atual, StatusPartida novo, LocalDateTime agora);
}
