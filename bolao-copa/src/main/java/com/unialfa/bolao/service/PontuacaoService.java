package com.unialfa.bolao.service;

import com.unialfa.bolao.model.CriterioPontuacao;
import com.unialfa.bolao.model.Palpite;
import com.unialfa.bolao.model.Partida;
import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.repository.PalpiteRepository;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PontuacaoService {

    private final PalpiteRepository palpiteRepository;
    private final UsuarioRepository usuarioRepository;

    public PontuacaoService(PalpiteRepository palpiteRepository, UsuarioRepository usuarioRepository) {
        this.palpiteRepository = palpiteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ResultadoPontuacao calcular(Integer pm, Integer pv, Integer rm, Integer rv) {
        if (pm != null && pv != null && rm != null && rv != null && pm.equals(rm) && pv.equals(rv)) {
            return new ResultadoPontuacao(10, CriterioPontuacao.PLACAR_EXATO);
        }

        if (pm != null && pv != null && rm != null && rv != null && Integer.signum(pm - pv) == Integer.signum(rm - rv)) {
            return new ResultadoPontuacao(5, CriterioPontuacao.VENCEDOR_OU_EMPATE);
        }

        return new ResultadoPontuacao(0, CriterioPontuacao.ERROU);
    }

    @Transactional
    public void recalcularPorPartida(Partida partida) {
        List<Palpite> palpites = palpiteRepository.findByPartidaId(partida.getId());
        Set<Long> usuariosAfetados = new HashSet<>();

        for (Palpite palpite : palpites) {
            ResultadoPontuacao resultado = calcular(
                    palpite.getGolsA(),
                    palpite.getGolsB(),
                    partida.getGolsA(),
                    partida.getGolsB()
            );
            palpite.setPontosObtidos(resultado.pontos());
            palpite.setCriterioPontuacao(resultado.criterio());
            palpiteRepository.save(palpite);
            usuariosAfetados.add(palpite.getUsuario().getId());
        }

        for (Long usuarioId : usuariosAfetados) {
            recalcularUsuario(usuarioId);
        }
    }

    @Transactional
    public void recalcularUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        List<Palpite> palpites = palpiteRepository.findByUsuarioId(usuarioId);

        int total = palpites.stream()
                .mapToInt(p -> p.getPontosObtidos() == null ? 0 : p.getPontosObtidos())
                .sum();

        int exatos = (int) palpites.stream()
                .filter(p -> p.getCriterioPontuacao() == CriterioPontuacao.PLACAR_EXATO)
                .count();

        usuario.setPontuacaoTotal(total);
        usuario.setPlacaresExatos(exatos);
        usuarioRepository.save(usuario);
    }

    public record ResultadoPontuacao(Integer pontos, CriterioPontuacao criterio) {}
}
