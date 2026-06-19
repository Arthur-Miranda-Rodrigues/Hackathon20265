package com.unialfa.bolao.service;

import com.unialfa.bolao.exception.BusinessException;
import com.unialfa.bolao.exception.NotFoundException;
import com.unialfa.bolao.model.FasePartida;
import com.unialfa.bolao.model.Partida;
import com.unialfa.bolao.model.StatusPartida;
import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.repository.PalpiteRepository;
import com.unialfa.bolao.repository.PartidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PartidaService {

    private final PartidaRepository repository;
    private final PalpiteRepository palpiteRepository;
    private final UsuarioService usuarioService;
    private final PontuacaoService pontuacaoService;

    public PartidaService(PartidaRepository repository,
                          PalpiteRepository palpiteRepository,
                          UsuarioService usuarioService,
                          PontuacaoService pontuacaoService) {
        this.repository = repository;
        this.palpiteRepository = palpiteRepository;
        this.usuarioService = usuarioService;
        this.pontuacaoService = pontuacaoService;
    }

    public List<Partida> listar() {
        return ordenar(repository.findAll());
    }

    public List<Partida> listar(FasePartida fase, LocalDate data, StatusPartida status) {
        return ordenar(repository.findAll().stream()
                .filter(p -> fase == null || p.getFase() == fase)
                .filter(p -> status == null || p.getStatus() == status)
                .filter(p -> data == null || (p.getDataHora() != null && p.getDataHora().toLocalDate().equals(data)))
                .toList());
    }

    public List<Partida> proximasParaPalpitar(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return repository.findByDataHoraAfterOrderByDataHoraAsc(LocalDateTime.now()).stream()
                .filter(partida -> !palpiteRepository.existsByUsuarioIdAndPartidaId(usuario.getId(), partida.getId()))
                .toList();
    }

    public Partida buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Partida não encontrada."));
    }

    @Transactional
    public Partida salvar(Partida partida) {
        if (partida.getStatus() == null) {
            partida.setStatus(StatusPartida.AGENDADA);
        }

        if (partida.getId() == null) {
            validar(partida);
            return repository.save(partida);
        }

        Partida atual = buscar(partida.getId());
        atual.setSelecaoA(partida.getSelecaoA());
        atual.setSelecaoB(partida.getSelecaoB());
        atual.setDataHora(partida.getDataHora());
        atual.setEstadio(partida.getEstadio());
        atual.setFase(partida.getFase());
        atual.setGrupo(partida.getGrupo());
        atual.setStatus(partida.getStatus());
        atual.setGolsA(partida.getGolsA());
        atual.setGolsB(partida.getGolsB());
        validar(atual);
        return repository.save(atual);
    }

    @Transactional
    public Partida salvarResultado(Long partidaId, Integer golsA, Integer golsB) {
        if (golsA == null || golsB == null || golsA < 0 || golsB < 0) {
            throw new BusinessException("Informe gols válidos para as duas seleções.");
        }
        Partida partida = buscar(partidaId);
        partida.setGolsA(golsA);
        partida.setGolsB(golsB);
        partida.setStatus(StatusPartida.ENCERRADA);
        Partida salva = repository.save(partida);
        pontuacaoService.recalcularPorPartida(salva);
        return salva;
    }

    @Transactional
    public void remover(Long id) {
        repository.deleteById(id);
    }

    public long totalPendentesResultado() {
        return repository.countByStatusNot(StatusPartida.ENCERRADA);
    }

    private List<Partida> ordenar(List<Partida> partidas) {
        return partidas.stream()
                .sorted(Comparator.comparing(Partida::getFase, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Partida::getDataHora, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private void validar(Partida partida) {
        if (partida.getSelecaoA() == null || partida.getSelecaoA().getId() == null) {
            throw new BusinessException("Seleção A é obrigatória.");
        }
        if (partida.getSelecaoB() == null || partida.getSelecaoB().getId() == null) {
            throw new BusinessException("Seleção B é obrigatória.");
        }
        if (partida.getSelecaoA().getId().equals(partida.getSelecaoB().getId())) {
            throw new BusinessException("A partida precisa ter duas seleções diferentes.");
        }
        if (partida.getDataHora() == null) {
            throw new BusinessException("Data e hora da partida são obrigatórias.");
        }
        if (partida.getFase() == null) {
            throw new BusinessException("Fase da partida é obrigatória.");
        }
        if (partida.getEstadio() == null || partida.getEstadio().isBlank()) {
            throw new BusinessException("Estádio é obrigatório.");
        }
    }
}
