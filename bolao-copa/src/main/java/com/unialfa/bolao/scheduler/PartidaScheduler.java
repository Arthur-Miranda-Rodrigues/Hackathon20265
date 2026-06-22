package com.unialfa.bolao.scheduler;

import com.unialfa.bolao.model.StatusPartida;
import com.unialfa.bolao.repository.PartidaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Transição automática de status das partidas (RF-011).
 * A cada minuto, partidas AGENDADA cujo horário oficial já chegou
 * (e que ainda não foram encerradas) passam para EM_ANDAMENTO.
 */
@Component
public class PartidaScheduler {

    private static final Logger log = LoggerFactory.getLogger(PartidaScheduler.class);

    private final PartidaRepository repository;

    public PartidaScheduler(PartidaRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void marcarPartidasEmAndamento() {
        int atualizadas = repository.atualizarStatusPorHorario(
                StatusPartida.AGENDADA, StatusPartida.EM_ANDAMENTO, LocalDateTime.now());
        if (atualizadas > 0) {
            log.info("{} partida(s) marcada(s) como EM_ANDAMENTO", atualizadas);
        }
    }
}
