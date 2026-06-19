package com.unialfa.bolao.service;

import com.unialfa.bolao.exception.BusinessException;
import com.unialfa.bolao.exception.NotFoundException;
import com.unialfa.bolao.model.Palpite;
import com.unialfa.bolao.model.Partida;
import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.repository.PalpiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PalpiteService {

    private final PalpiteRepository repository;
    private final PartidaService partidaService;
    private final UsuarioService usuarioService;

    public PalpiteService(PalpiteRepository repository, PartidaService partidaService, UsuarioService usuarioService) {
        this.repository = repository;
        this.partidaService = partidaService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Palpite registrar(String email, Long partidaId, Integer golsA, Integer golsB) {
        validarGols(golsA, golsB);
        Usuario usuario = usuarioService.buscarPorEmail(email);
        Partida partida = partidaService.buscar(partidaId);
        validarAntesDoInicio(partida);

        if (repository.existsByUsuarioIdAndPartidaId(usuario.getId(), partida.getId())) {
            throw new BusinessException("Usuário já possui palpite para esta partida.");
        }

        Palpite palpite = new Palpite();
        palpite.setUsuario(usuario);
        palpite.setPartida(partida);
        palpite.setGolsA(golsA);
        palpite.setGolsB(golsB);
        return repository.save(palpite);
    }

    @Transactional
    public Palpite editar(String email, Long palpiteId, Integer golsA, Integer golsB) {
        validarGols(golsA, golsB);
        Usuario usuario = usuarioService.buscarPorEmail(email);
        Palpite palpite = buscar(palpiteId);

        if (!palpite.getUsuario().getId().equals(usuario.getId())) {
            throw new BusinessException("Este palpite pertence a outro usuário.");
        }

        validarAntesDoInicio(palpite.getPartida());
        palpite.setGolsA(golsA);
        palpite.setGolsB(golsB);
        palpite.setPontosObtidos(0);
        palpite.setCriterioPontuacao(null);
        return repository.save(palpite);
    }

    public Palpite buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Palpite não encontrado."));
    }

    public List<Palpite> meus(String email) {
        return repository.findByUsuarioEmailOrderByPartidaDataHoraAsc(email);
    }

    public long totalPalpites() {
        return repository.count();
    }

    private void validarAntesDoInicio(Partida partida) {
        if (!LocalDateTime.now().isBefore(partida.getDataHora())) {
            throw new BusinessException("Não é possível registrar ou editar palpite após o início da partida.");
        }
    }

    private void validarGols(Integer golsA, Integer golsB) {
        if (golsA == null || golsB == null || golsA < 0 || golsB < 0) {
            throw new BusinessException("Informe gols válidos para as duas seleções.");
        }
    }
}
