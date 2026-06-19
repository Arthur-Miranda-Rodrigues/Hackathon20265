package com.unialfa.bolao.service;

import com.unialfa.bolao.model.PerfilUsuario;
import com.unialfa.bolao.model.Usuario;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {

    private final UsuarioRepository usuarioRepository;

    public RankingService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Page<Usuario> ranking(int page, int size) {
        int tamanho = Math.max(size, 50);
        Pageable pageable = PageRequest.of(page, tamanho);
        return usuarioRepository.findByPerfilOrderByPontuacaoTotalDescPlacaresExatosDescCriadoEmAsc(PerfilUsuario.USER, pageable);
    }

    public PosicaoUsuario posicaoUsuario(String email) {
        List<Usuario> todos = usuarioRepository.findByPerfilOrderByPontuacaoTotalDescPlacaresExatosDescCriadoEmAsc(PerfilUsuario.USER);
        for (int i = 0; i < todos.size(); i++) {
            Usuario usuario = todos.get(i);
            if (usuario.getEmail().equals(email)) {
                return new PosicaoUsuario(i + 1, usuario.getNome(), usuario.getPontuacaoTotal(), usuario.getPlacaresExatos());
            }
        }
        return null;
    }

    public record PosicaoUsuario(Integer posicao, String nome, Integer pontuacaoTotal, Integer placaresExatos) {}
}
